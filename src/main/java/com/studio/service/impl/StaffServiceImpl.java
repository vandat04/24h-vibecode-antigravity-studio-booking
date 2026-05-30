package com.studio.service.impl;

import com.studio.constant.BookingStatus;
import com.studio.constant.PaymentStatus;
import com.studio.dto.request.ChangePasswordRequest;
import com.studio.dto.request.PostProductionUpdateRequest;
import com.studio.dto.response.AdminBookingResponse;
import com.studio.entity.*;
import com.studio.repository.*;
import com.studio.security.CustomPasswordEncoder;
import com.studio.service.StaffService;
import com.studio.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingAssignmentRepository bookingAssignmentRepository;
    private final PostProductionHistoryRepository postProductionHistoryRepository;
    private final BookingStatusHistoryRepository bookingStatusHistoryRepository;
    private final CustomPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private User getAuthenticatedUser() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new AccessDeniedException("Vui lòng đăng nhập để thực hiện thao tác.");
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            throw new AccessDeniedException("Vui lòng đăng nhập để thực hiện thao tác.");
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Không tìm thấy tài khoản người dùng đăng nhập."));
    }

    private void checkBookingOwnership(Long bookingId, User staff) {
        if (staff.getRole().getRoleName().equalsIgnoreCase("ADMIN")) {
            return; // Admin có toàn quyền
        }
        boolean isAssigned = bookingAssignmentRepository.findByBookingId(bookingId).stream()
                .anyMatch(a -> a.getStaff().getId().equals(staff.getId()));
        if (!isAssigned) {
            throw new AccessDeniedException("Bạn không có quyền truy cập ca làm việc này vì không được phân công.");
        }
    }

    @Override
    public List<AdminBookingResponse> getMyBookings() {
        User staff = getAuthenticatedUser();
        List<BookingAssignment> assignments = bookingAssignmentRepository.findByStaffId(staff.getId());
        
        return assignments.stream()
                .map(BookingAssignment::getBooking)
                .map(this::toAdminBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AdminBookingResponse getMyBookingDetail(Long bookingId) {
        User staff = getAuthenticatedUser();
        checkBookingOwnership(bookingId, staff);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca làm việc với ID: " + bookingId));

        return toAdminBookingResponse(booking);
    }

    @Override
    @Transactional
    public void confirmMakeupComplete(Long bookingId) {
        User staff = getAuthenticatedUser();
        if (!staff.getRole().getRoleName().equalsIgnoreCase("MAKEUP") && !staff.getRole().getRoleName().equalsIgnoreCase("ADMIN")) {
            throw new AccessDeniedException("Chỉ chuyên viên trang điểm (MAKEUP) mới được quyền thực hiện chức năng này.");
        }
        checkBookingOwnership(bookingId, staff);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca làm việc với ID: " + bookingId));

        // Ghi nhận audit log lịch sử hoàn thành makeup
        BookingStatusHistory history = BookingStatusHistory.builder()
                .booking(booking)
                .previousStatus(booking.getBookingStatus())
                .newStatus(booking.getBookingStatus()) // Giữ nguyên trạng thái booking, chỉ thêm log
                .note("Chuyên viên trang điểm " + staff.getFullName() + " đã hoàn thành trang điểm cho khách hàng, sẵn sàng chụp hình.")
                .changedBy(staff)
                .build();
        
        bookingStatusHistoryRepository.save(history);
    }

    @Override
    @Transactional
    public PostProductionHistory updateMyPostProduction(Long bookingId, PostProductionUpdateRequest request) {
        User staff = getAuthenticatedUser();
        if (!staff.getRole().getRoleName().equalsIgnoreCase("PHOTOGRAPHER") && !staff.getRole().getRoleName().equalsIgnoreCase("ADMIN")) {
            throw new AccessDeniedException("Chỉ nhiếp ảnh gia (PHOTOGRAPHER) mới được quyền cập nhật tiến độ hậu kỳ.");
        }
        checkBookingOwnership(bookingId, staff);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca làm việc với ID: " + bookingId));

        // Photographer cập nhật hậu kỳ
        PostProductionHistory history = PostProductionHistory.builder()
                .booking(booking)
                .productionStatus(request.getProductionStatus())
                .rawPhotoLink(request.getRawPhotoLink())
                .editedPhotoLink(request.getEditedPhotoLink())
                .note(request.getNote())
                .updatedBy(staff)
                .build();

        PostProductionHistory saved = postProductionHistoryRepository.save(history);

        // Tự động cập nhật trạng thái đơn chụp sang EDITING nếu Photographer bắt đầu hậu kỳ
        if (booking.getBookingStatus() == BookingStatus.ASSIGNED || booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            booking.setBookingStatus(BookingStatus.EDITING);
            bookingRepository.save(booking);
            
            // Ghi audit history
            BookingStatusHistory bookingHistory = BookingStatusHistory.builder()
                    .booking(booking)
                    .previousStatus(BookingStatus.ASSIGNED)
                    .newStatus(BookingStatus.EDITING)
                    .note("Nhiếp ảnh gia bắt đầu xử lý hậu kỳ hình ảnh.")
                    .changedBy(staff)
                    .build();
            bookingStatusHistoryRepository.save(bookingHistory);
        }

        // Nếu trạng thái là bàn giao DELIVERED, tự động gửi email chứa link ảnh cho khách hàng
        if (request.getProductionStatus() == com.studio.constant.ProductionStatus.DELIVERED && request.getEditedPhotoLink() != null) {
            emailService.sendPhotosDelivered(booking, request.getEditedPhotoLink());
        }

        return saved;
    }

    @Override
    @Transactional
    public void changeMyPassword(ChangePasswordRequest request) {
        User staff = getAuthenticatedUser();
        if (!passwordEncoder.matches(request.getOldPassword(), staff.getPasswordHash())) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác.");
        }
        staff.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(staff);
    }

    private AdminBookingResponse toAdminBookingResponse(Booking booking) {
        // Lấy danh sách các nhân sự được phân công ca chụp này
        List<AdminBookingResponse.AssignedStaffItem> assignedStaff = bookingAssignmentRepository.findByBookingId(booking.getId())
                .stream()
                .map(a -> AdminBookingResponse.AssignedStaffItem.builder()
                        .staffId(a.getStaff().getId())
                        .fullName(a.getStaff().getFullName())
                        .role(a.getStaff().getRole().getRoleName())
                        .build())
                .collect(Collectors.toList());

        return AdminBookingResponse.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .customerName(booking.getCustomerName())
                .customerEmail(booking.getCustomerEmail())
                .customerPhone(booking.getCustomerPhone())
                .customerFacebook(booking.getCustomerFacebook())
                .shootDate(booking.getShootDate())
                .shootTimeSlot(booking.getShootTimeSlot())
                .shootLocation(booking.getShootLocation())
                .customerNotes(booking.getCustomerNotes())
                .totalAmount(booking.getTotalAmount())
                .bookingStatus(booking.getBookingStatus())
                .paymentStatus(booking.getPaymentStatus())
                .createdAt(booking.getCreatedAt())
                .packageName(booking.getServicePackage().getPackageName())
                .conceptTitle(booking.getConcept().getTitle())
                .assignedStaff(assignedStaff)
                .build();
    }
}
