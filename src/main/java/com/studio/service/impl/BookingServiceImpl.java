package com.studio.service.impl;

import com.studio.constant.BookingStatus;
import com.studio.constant.PaymentStatus;
import com.studio.entity.*;
import com.studio.repository.*;
import com.studio.service.BookingService;
import com.studio.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final ConceptRepository conceptRepository;
    private final UserRepository userRepository;
    private final BookingStatusHistoryRepository bookingStatusHistoryRepository;
    private final EmailService emailService;
    private final BookingHoldRepository bookingHoldRepository;

    private long getMaxParallelBookings() {
        long photographers = userRepository.findByRoleRoleName("PHOTOGRAPHER").stream()
                .filter(User::getIsActive)
                .count();
        long makeups = userRepository.findByRoleRoleName("MAKEUP").stream()
                .filter(User::getIsActive)
                .count();
        long capacity = Math.min(photographers, makeups);
        return capacity > 0 ? capacity : 2; // Mặc định cho phép 2 ca song song nếu dữ liệu trống
    }

    @Override
    @Transactional
    public Booking createBooking(Booking booking, Long packageId, Long conceptId, String holdToken) {
        // 1. Kiểm tra nghiêm ngặt ngày chụp (shootDate phải lớn hơn hoặc bằng ngày hiện tại)
        if (booking.getShootDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày chụp (shootDate) phải lớn hơn hoặc bằng ngày hiện tại.");
        }

        // 2. Tự động dọn dẹp các slot giữ chỗ quá hạn
        bookingHoldRepository.deleteExpiredHolds(LocalDateTime.now());

        // 3. Lấy thông tin Gói dịch vụ và Concept
        ServicePackage servicePackage = servicePackageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Service package not found with ID: " + packageId));
        Concept concept = conceptRepository.findById(conceptId)
                .orElseThrow(() -> new IllegalArgumentException("Concept not found with ID: " + conceptId));

        booking.setServicePackage(servicePackage);
        booking.setConcept(concept);

        // 4. Tìm các Bookings và Holds hiện tại của slot
        List<Booking> bookings = bookingRepository
                .findByShootDateAndShootTimeSlot(booking.getShootDate(), booking.getShootTimeSlot())
                .stream()
                .filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED)
                .collect(Collectors.toList());

        List<BookingHold> holds = bookingHoldRepository
                .findByShootDateAndShootTimeSlotAndHoldExpiredAtAfter(booking.getShootDate(), booking.getShootTimeSlot(), LocalDateTime.now());

        // 5. Xác định xem khách hàng có sở hữu holdToken hợp lệ không
        boolean hasValidHold = false;
        BookingHold clientHold = null;
        if (holdToken != null) {
            Optional<BookingHold> matchingHold = bookingHoldRepository
                    .findByHoldTokenAndHoldExpiredAtAfter(holdToken, LocalDateTime.now());
            if (matchingHold.isPresent() 
                && matchingHold.get().getShootDate().equals(booking.getShootDate()) 
                && matchingHold.get().getShootTimeSlot().equals(booking.getShootTimeSlot())) {
                hasValidHold = true;
                clientHold = matchingHold.get();
            }
        }

        // 6. Bước A: Kiểm tra kẹt bối cảnh Concept
        long bookedConceptCount = bookings.stream()
                .filter(b -> b.getConcept().getId().equals(conceptId))
                .count();

        // Tính effective holds của riêng concept này (loại trừ hold của chính khách hàng này nếu khớp token)
        final BookingHold finalClientHold = clientHold;
        long effectiveConceptHolds = holds.stream()
                .filter(h -> conceptId.equals(h.getConceptId()) && (finalClientHold == null || !h.getId().equals(finalClientHold.getId())))
                .count();

        if (bookedConceptCount + effectiveConceptHolds >= 1) {
            throw new IllegalStateException("Khung giờ " + booking.getShootTimeSlot() + " ngày " + booking.getShootDate() 
                    + " đã có khách hàng khác đặt bối cảnh này. Vui lòng chọn Concept hoặc khung giờ khác.");
        }

        // 7. Bước B: Kiểm tra sức chứa động theo gói dịch vụ
        long maxCapacity = getMaxParallelBookings();
        long photographers = userRepository.findByRoleRoleName("PHOTOGRAPHER").stream()
                .filter(User::getIsActive)
                .count();

        if (servicePackage.getMakeupPersonCount() == 0) {
            maxCapacity = photographers > 0 ? photographers : 2;
        } else {
            long makeups = userRepository.findByRoleRoleName("MAKEUP").stream()
                    .filter(User::getIsActive)
                    .count();
            long capacity = Math.min(photographers, makeups);
            maxCapacity = capacity > 0 ? capacity : 2;
        }

        long activeBookings = bookings.size();
        long activeHolds = holds.size();
        long effectiveHolds = activeHolds;
        if (hasValidHold) {
            effectiveHolds = Math.max(0, activeHolds - 1);
        }

        if (activeBookings + effectiveHolds >= maxCapacity) {
            throw new IllegalStateException("Khung giờ " + booking.getShootTimeSlot()
                    + " ngày " + booking.getShootDate() + " đã đạt giới hạn phục vụ tối đa của ê-kíp (" + maxCapacity + " ca song song). Vui lòng chọn khung giờ khác.");
        }
        
        // 3. Tính tổng tiền từ gói dịch vụ
        booking.setTotalAmount(servicePackage.getPrice());
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.UNPAID);

        // 4. Hệ thống tự động sinh Booking Code độc bản (STB-yyyyMMdd-XXXXX)
        String bookingCode;
        boolean codeExists;
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        do {
            String randomSuffix = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            bookingCode = "STB-" + dateStr + "-" + randomSuffix;
            codeExists = bookingRepository.findByBookingCode(bookingCode).isPresent();
        } while (codeExists);
        booking.setBookingCode(bookingCode);

        // 5. Lưu đơn đặt lịch
        Booking savedBooking = bookingRepository.save(booking);

        // Giải phóng slot giữ chỗ tạm thời vì đã được book chính thức
        bookingHoldRepository.deleteHoldsOnSlot(savedBooking.getShootDate(), savedBooking.getShootTimeSlot());

        // 6. Ghi lịch sử thay đổi trạng thái (Audit Trail History)
        User systemUser = userRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy tài khoản người dùng hệ thống để ghi nhận lịch sử đặt lịch."));

        BookingStatusHistory history = BookingStatusHistory.builder()
                .booking(savedBooking)
                .previousStatus(null)
                .newStatus(BookingStatus.PENDING)
                .note("Đơn đặt lịch được tạo trực tuyến thành công bởi Khách hàng.")
                .changedBy(systemUser)
                .build();
        bookingStatusHistoryRepository.save(history);

        // 7. Gửi email xác nhận (non-blocking - thất bại do mail server sẽ được bắt lại và không rollback transaction)
        emailService.sendBookingConfirmation(savedBooking);

        return savedBooking;
    }

    @Override
    @Transactional
    public Booking updateBookingStatus(Long bookingId, BookingStatus newStatus, String note, Long changedByUserId) {
        Booking booking = getBookingById(bookingId);
        BookingStatus oldStatus = booking.getBookingStatus();

        if (oldStatus == newStatus) {
            return booking;
        }

        User changer = userRepository.findById(changedByUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + changedByUserId));

        booking.setBookingStatus(newStatus);
        Booking updatedBooking = bookingRepository.save(booking);

        // Log history
        BookingStatusHistory history = BookingStatusHistory.builder()
                .booking(updatedBooking)
                .previousStatus(oldStatus)
                .newStatus(newStatus)
                .note(note)
                .changedBy(changer)
                .build();
        bookingStatusHistoryRepository.save(history);

        return updatedBooking;
    }

    @Override
    @Transactional
    public Booking updatePaymentStatus(Long bookingId, PaymentStatus newStatus) {
        Booking booking = getBookingById(bookingId);
        booking.setPaymentStatus(newStatus);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + id));
    }

    @Override
    public Booking getBookingByCode(String code) {
        return bookingRepository.findByBookingCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with code: " + code));
    }

    @Override
    public List<Booking> getBookingsByPhone(String phone) {
        return bookingRepository.findByCustomerPhone(phone);
    }
}
