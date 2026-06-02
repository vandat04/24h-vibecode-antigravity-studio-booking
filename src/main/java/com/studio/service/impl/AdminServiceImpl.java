package com.studio.service.impl;

import com.studio.constant.*;
import com.studio.dto.request.*;
import com.studio.dto.response.*;
import com.studio.entity.*;
import com.studio.repository.*;
import com.studio.security.CustomPasswordEncoder;
import com.studio.service.AdminService;
import com.studio.service.CloudinaryService;
import com.studio.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final BookingRepository bookingRepository;
    private final BookingAssignmentRepository bookingAssignmentRepository;
    private final BookingStatusHistoryRepository bookingStatusHistoryRepository;
    private final PostProductionHistoryRepository postProductionHistoryRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StaffProfileRepository staffProfileRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final ConceptRepository conceptRepository;
    private final ConceptImageRepository conceptImageRepository;
    private final BlogRepository blogRepository;
    private final CustomerStoryRepository customerStoryRepository;
    private final StudioInformationRepository studioInformationRepository;
    private final CustomPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CloudinaryService cloudinaryService;

    // =========================================================
    // I. QUẢN LÝ LỊCH HẸN (BOOKING LIFECYCLE)
    // =========================================================

    @Override
    public List<AdminBookingResponse> getBookings(int page, int size, BookingStatus status) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<Booking> bookings;
        if (status != null) {
            bookings = bookingRepository.findByBookingStatus(status); // For simplicity, we filter in memory or fetch directly
        } else {
            bookings = bookingRepository.findAll(pageRequest).getContent();
        }

        return bookings.stream().map(this::toAdminBookingResponse).collect(Collectors.toList());
    }

    @Override
    public AdminBookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt lịch với ID: " + id));
        return toAdminBookingResponse(booking);
    }

    @Override
    @Transactional
    public AdminBookingResponse updateBookingStatus(Long id, BookingStatus status, String note) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt lịch với ID: " + id));

        BookingStatus oldStatus = booking.getBookingStatus();
        if (oldStatus == status) {
            return toAdminBookingResponse(booking);
        }

        booking.setBookingStatus(status);
        Booking saved = bookingRepository.save(booking);

        // Ghi nhận audit log lịch sử thay đổi trạng thái
        User changedBy = getAuthenticatedUser();
        BookingStatusHistory history = BookingStatusHistory.builder()
                .booking(saved)
                .previousStatus(oldStatus)
                .newStatus(status)
                .note(note != null ? note : "Admin cập nhật trạng thái đơn chụp.")
                .changedBy(changedBy)
                .build();
        bookingStatusHistoryRepository.save(history);

        // Gửi email thông báo cho khách hàng
        emailService.sendBookingStatusUpdate(saved, note);

        return toAdminBookingResponse(saved);
    }

    @Override
    @Transactional
    public AdminBookingResponse updatePaymentStatus(Long id, PaymentStatus status, String method) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt lịch với ID: " + id));

        PaymentStatus oldStatus = booking.getPaymentStatus();
        booking.setPaymentStatus(status);

        // Tự động chuyển trạng thái đơn đặt lịch sang CONFIRMED khi khách đã đặt cọc hoặc thanh toán xong
        if ((status == PaymentStatus.DEPOSITED || status == PaymentStatus.PAID) && booking.getBookingStatus() == BookingStatus.PENDING) {
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            
            // Ghi nhận audit log tự động chuyển trạng thái
            User systemUser = getAuthenticatedUser();
            BookingStatusHistory history = BookingStatusHistory.builder()
                    .booking(booking)
                    .previousStatus(BookingStatus.PENDING)
                    .newStatus(BookingStatus.CONFIRMED)
                    .note("Hệ thống tự động Xác nhận (CONFIRMED) sau khi ghi nhận giao dịch tài chính: " + status.name())
                    .changedBy(systemUser)
                    .build();
            bookingStatusHistoryRepository.save(history);
        }

        Booking saved = bookingRepository.save(booking);
        emailService.sendPaymentUpdate(saved, method);
        return toAdminBookingResponse(saved);
    }

    @Override
    @Transactional
    public AdminBookingResponse assignStaff(Long id, Long photographerId, Long makeupId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt lịch với ID: " + id));

        LocalDate date = booking.getShootDate();
        LocalTime slot = booking.getShootTimeSlot();

        User photographer = userRepository.findById(photographerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thợ chụp ảnh với ID: " + photographerId));
        User makeup = userRepository.findById(makeupId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thợ trang điểm với ID: " + makeupId));

        // 1. Kiểm tra kẹt lịch thợ chụp ảnh
        boolean photoBusy = bookingAssignmentRepository.findByStaffId(photographerId).stream()
                .anyMatch(a -> a.getShootDate().equals(date) && a.getShootTimeSlot().equals(slot) && !a.getBooking().getId().equals(id));
        if (photoBusy) {
            throw new IllegalStateException("Không thể phân công: Thợ chụp ảnh " + photographer.getFullName() + " đã kẹt ca làm việc khác vào cùng thời điểm này.");
        }

        // 2. Kiểm tra kẹt lịch thợ trang điểm
        boolean makeupBusy = bookingAssignmentRepository.findByStaffId(makeupId).stream()
                .anyMatch(a -> a.getShootDate().equals(date) && a.getShootTimeSlot().equals(slot) && !a.getBooking().getId().equals(id));
        if (makeupBusy) {
            throw new IllegalStateException("Không thể phân công: Thợ trang điểm " + makeup.getFullName() + " đã kẹt ca làm việc khác vào cùng thời điểm này.");
        }

        // 3. Xóa các phân công nhân sự cũ của booking này nếu có
        List<BookingAssignment> oldAssignments = bookingAssignmentRepository.findByBookingId(id);
        bookingAssignmentRepository.deleteAll(oldAssignments);

        // 4. Tạo các phân công nhân sự mới
        BookingAssignment assignPhoto = BookingAssignment.builder()
                .booking(booking)
                .staff(photographer)
                .shootDate(date)
                .shootTimeSlot(slot)
                .build();

        BookingAssignment assignMakeup = BookingAssignment.builder()
                .booking(booking)
                .staff(makeup)
                .shootDate(date)
                .shootTimeSlot(slot)
                .build();

        bookingAssignmentRepository.save(assignPhoto);
        bookingAssignmentRepository.save(assignMakeup);

        // 5. Chuyển trạng thái booking sang ASSIGNED
        booking.setBookingStatus(BookingStatus.ASSIGNED);
        Booking saved = bookingRepository.save(booking);

        // Ghi audit history
        BookingStatusHistory history = BookingStatusHistory.builder()
                .booking(saved)
                .previousStatus(BookingStatus.CONFIRMED)
                .newStatus(BookingStatus.ASSIGNED)
                .note("Phân công nhân sự: " + photographer.getFullName() + " (Photo) & " + makeup.getFullName() + " (Makeup)")
                .changedBy(getAuthenticatedUser())
                .build();
        bookingStatusHistoryRepository.save(history);
        emailService.sendStaffAssignment(saved);

        return toAdminBookingResponse(saved);
    }

    @Override
    public List<BookingStatusHistory> getBookingHistory(Long id) {
        return bookingStatusHistoryRepository.findByBookingId(id);
    }

    // =========================================================
    // II. GIÁM SÁT HẬU KỲ (POST-PRODUCTION)
    // =========================================================

    @Override
    public List<PostProductionHistory> getPostProductions(ProductionStatus status, int page, int size) {
        List<PostProductionHistory> list = postProductionHistoryRepository.findAllWithBookingAndUser();
        if (status != null) {
            list = list.stream()
                    .filter(p -> p.getProductionStatus().name().equals(status.name()))
                    .collect(Collectors.toList());
        }
        return list.stream()
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostProductionHistory updatePostProduction(Long bookingId, PostProductionUpdateRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt lịch với ID: " + bookingId));

        // Tìm bản ghi hậu kỳ hiện tại (bản ghi mới nhất) của booking này
        List<PostProductionHistory> existing = postProductionHistoryRepository.findByBookingIdOrderByUpdatedAtDesc(bookingId);

        PostProductionHistory record;
        if (!existing.isEmpty()) {
            // Cập nhật bản ghi hiện tại thay vì tạo mới
            record = existing.get(0);
            record.setProductionStatus(request.getProductionStatus());
            record.setRawPhotoLink(request.getRawPhotoLink());
            record.setEditedPhotoLink(request.getEditedPhotoLink());
            record.setNote(request.getNote());
            record.setUpdatedBy(getAuthenticatedUser());
        } else {
            // Không có bản ghi nào, tạo mới lần đầu
            record = PostProductionHistory.builder()
                    .booking(booking)
                    .productionStatus(request.getProductionStatus())
                    .rawPhotoLink(request.getRawPhotoLink())
                    .editedPhotoLink(request.getEditedPhotoLink())
                    .note(request.getNote())
                    .updatedBy(getAuthenticatedUser())
                    .build();
        }

        PostProductionHistory saved = postProductionHistoryRepository.save(record);

        // Nếu trạng thái là bàn giao DELIVERED
        if (request.getProductionStatus() == ProductionStatus.DELIVERED) {
            BookingStatus oldStatus = booking.getBookingStatus();
            if (oldStatus != BookingStatus.COMPLETED) {
                booking.setBookingStatus(BookingStatus.COMPLETED);
                bookingRepository.save(booking);

                // Ghi audit history chuyển sang hoàn thành
                BookingStatusHistory completedHistory = BookingStatusHistory.builder()
                        .booking(booking)
                        .previousStatus(oldStatus)
                        .newStatus(BookingStatus.COMPLETED)
                        .note("Hệ thống tự động chuyển sang hoàn thành (COMPLETED) sau khi Admin bàn giao sản phẩm hình ảnh hoàn thiện.")
                        .changedBy(getAuthenticatedUser())
                        .build();
                bookingStatusHistoryRepository.save(completedHistory);
            }

            // Gửi email bàn giao link ảnh
            if (request.getEditedPhotoLink() != null && !request.getEditedPhotoLink().isBlank()) {
                emailService.sendPhotosDelivered(booking, request.getEditedPhotoLink());
            }

            // Gửi email thông báo hoàn thành
            emailService.sendBookingStatusUpdate(booking, "Bộ ảnh hoàn thiện của bạn đã được Admin bàn giao thành công. Leon Studio trân trọng cảm ơn quý khách!");
        }
        // Trạng thái trung gian khác
        else {
            if (request.getProductionStatus() == ProductionStatus.WAITING_APPROVAL) {
                emailService.sendBookingStatusUpdate(booking, "Bộ ảnh của bạn đã được chuyển sang trạng thái 'Chờ khách duyệt'. Vui lòng kiểm tra và phản hồi với Studio.");
            } else if (request.getProductionStatus() == ProductionStatus.EDITING) {
                emailService.sendBookingStatusUpdate(booking, "Tiến độ hậu kỳ của bộ ảnh đã được chuyển sang trạng thái 'Đang chỉnh sửa'.");
            }
        }

        return saved;
    }

    // =========================================================
    // III. BÁO CÁO & THỐNG KÊ (DASHBOARD)
    // =========================================================

    @Override
    public RevenueResponse getRevenueReport(LocalDate startDate, LocalDate endDate) {
        List<Booking> bookings = bookingRepository.findBookingsForRevenue(startDate, endDate);

        // Tính tổng doanh thu thực tế từ các đơn đã cọc hoặc thanh toán đủ
        BigDecimal totalRevenue = bookings.stream()
                .filter(b -> b.getPaymentStatus() == PaymentStatus.DEPOSITED || b.getPaymentStatus() == PaymentStatus.PAID)
                .map(Booking::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Doanh thu chi tiết theo ngày
        Map<LocalDate, BigDecimal> revenueMap = bookings.stream()
                .filter(b -> b.getPaymentStatus() == PaymentStatus.DEPOSITED || b.getPaymentStatus() == PaymentStatus.PAID)
                .collect(Collectors.groupingBy(
                        Booking::getShootDate,
                        Collectors.reducing(BigDecimal.ZERO, Booking::getTotalAmount, BigDecimal::add)
                ));

        List<RevenueResponse.RevenueByDateItem> revenueByDate = revenueMap.entrySet().stream()
                .map(e -> RevenueResponse.RevenueByDateItem.builder()
                        .date(e.getKey().toString())
                        .amount(e.getValue())
                        .build())
                .sorted(Comparator.comparing(RevenueResponse.RevenueByDateItem::getDate))
                .collect(Collectors.toList());

        // Độ phổ biến gói dịch vụ
        List<Object[]> popularityData = bookingRepository.findPackagePopularity();
        List<RevenueResponse.PackagePopularityItem> packagePopularity = popularityData.stream()
                .map(row -> RevenueResponse.PackagePopularityItem.builder()
                        .packageName((String) row[0])
                        .bookingCount(row[1] != null ? ((Number) row[1]).longValue() : 0L)
                        .build())
                .collect(Collectors.toList());

        return RevenueResponse.builder()
                .totalRevenue(totalRevenue)
                .totalBookings(bookings.size())
                .revenueByDate(revenueByDate)
                .packagePopularity(packagePopularity)
                .build();
    }

    @Override
    public Map<String, Object> getDashboardStatistics() {
        long totalStaff = staffProfileRepository.count();
        long totalBookings = bookingRepository.count();
        long pendingBookings = bookingRepository.findByBookingStatus(BookingStatus.PENDING).size();
        long activePackages = servicePackageRepository.findByIsActiveTrue().size();

        return Map.of(
                "totalStaff", totalStaff,
                "totalBookings", totalBookings,
                "pendingBookings", pendingBookings,
                "activePackages", activePackages
        );
    }

    // =========================================================
    // IV. QUẢN LÝ NHÂN SỰ & KHÁCH HÀNG
    // =========================================================

    @Override
    public List<StaffProfileResponse> getAllStaff(int page, int size) {
        return getAllStaff(page, size, null);
    }

    // Overload with optional role filter
    public List<StaffProfileResponse> getAllStaff(int page, int size, String roleName) {
        List<User> users = userRepository.findAll();
        
        if (roleName != null && !roleName.isBlank()) {
            String upperRole = roleName.toUpperCase();
            users = users.stream()
                    .filter(u -> u.getRole().getRoleName().equalsIgnoreCase(upperRole))
                    .collect(Collectors.toList());
        }

        // Sắp xếp theo thứ tự ưu tiên: ADMIN -> PHOTOGRAPHER -> MAKEUP -> MEDIA
        users.sort(java.util.Comparator.comparingInt(u -> getRoleSortWeight(u.getRole().getRoleName())));

        return users.stream()
                .skip((long) page * size)
                .limit(size)
                .map(u -> {
                    StaffProfile p = u.getStaffProfile();
                    return StaffProfileResponse.builder()
                            .profileId(p != null ? p.getId() : u.getId())
                            .userId(u.getId())
                            .username(u.getUsername())
                            .fullName(u.getFullName())
                            .email(u.getEmail())
                            .phone(u.getPhone())
                            .roleName(u.getRole().getRoleName())
                            .avatarUrl(p != null ? p.getAvatarUrl() : "https://res.cloudinary.com/do8uakd0l/image/upload/v1780213774/hai_m8zhf6.webp")
                            .bio(p != null ? p.getBio() : "Sáng lập & Điều hành LEON STUDIO")
                            .experienceDetail(p != null ? p.getExperienceDetail() : "Chỉ đạo nghệ thuật & Quản lý")
                            .yearsOfExperience(p != null ? p.getYearsOfExperience() : 10)
                            .facebookUrl(p != null ? p.getFacebookUrl() : null)
                            .instagramUrl(p != null ? p.getInstagramUrl() : null)
                            .tiktokUrl(p != null ? p.getTiktokUrl() : null)
                            .isActive(u.getIsActive())
                            .isDisplayed(p != null ? p.getIsDisplayed() : true)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private int getRoleSortWeight(String roleName) {
        if (roleName == null) return 99;
        switch (roleName.toUpperCase()) {
            case "ADMIN": return 1;
            case "PHOTOGRAPHER": return 2;
            case "MAKEUP": return 3;
            case "MEDIA": return 4;
            default: return 99;
        }
    }


    @Override
    @Transactional
    public StaffProfileResponse createStaff(StaffCreateRequest request) {

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò với ID: " + request.getRoleId()));

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(role)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        StaffProfile profile = StaffProfile.builder()
                .user(savedUser)
                .avatarUrl(request.getAvatarUrl())
                .bio(request.getBio())
                .experienceDetail(request.getExperienceDetail())
                .yearsOfExperience(request.getYearsOfExperience() != null ? request.getYearsOfExperience() : 0)
                .facebookUrl(request.getFacebookUrl())
                .instagramUrl(request.getInstagramUrl())
                .tiktokUrl(request.getTiktokUrl())
                .isDisplayed(request.getIsDisplayed())
                .build();

        StaffProfile savedProfile = staffProfileRepository.save(profile);

        return StaffProfileResponse.builder()
                .profileId(savedProfile.getId())
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .roleName(role.getRoleName())
                .avatarUrl(savedProfile.getAvatarUrl())
                .bio(savedProfile.getBio())
                .experienceDetail(savedProfile.getExperienceDetail())
                .yearsOfExperience(savedProfile.getYearsOfExperience())
                .facebookUrl(savedProfile.getFacebookUrl())
                .instagramUrl(savedProfile.getInstagramUrl())
                .tiktokUrl(savedProfile.getTiktokUrl())
                .isActive(savedUser.getIsActive())
                .isDisplayed(savedProfile.getIsDisplayed())
                .build();
    }

    @Override
    @Transactional
    public StaffProfileResponse updateStaff(Long id, StaffUpdateRequest updatedProfile) {
        StaffProfile profile = staffProfileRepository.findById(id).orElse(null);
        if (profile == null) {
            profile = staffProfileRepository.findByUserId(id).orElse(null);
        }

        User user;
        if (profile == null) {
            // Create a brand new StaffProfile for this user (e.g. for the ADMIN)
            user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản nhân viên với ID: " + id));
            profile = StaffProfile.builder()
                    .user(user)
                    .avatarUrl(updatedProfile.getAvatarUrl() != null ? updatedProfile.getAvatarUrl() : "https://res.cloudinary.com/do8uakd0l/image/upload/v1780213774/hai_m8zhf6.webp")
                    .bio(updatedProfile.getBio() != null ? updatedProfile.getBio() : "")
                    .experienceDetail(updatedProfile.getExperienceDetail())
                    .yearsOfExperience(updatedProfile.getYearsOfExperience() != null ? updatedProfile.getYearsOfExperience() : 10)
                    .facebookUrl(updatedProfile.getFacebookUrl())
                    .instagramUrl(updatedProfile.getInstagramUrl())
                    .tiktokUrl(updatedProfile.getTiktokUrl())
                    .isDisplayed(updatedProfile.getIsDisplayed() != null ? updatedProfile.getIsDisplayed() : true)
                    .build();
        } else {
            user = profile.getUser();
            // Update StaffProfile fields
            if (updatedProfile.getAvatarUrl() != null) profile.setAvatarUrl(updatedProfile.getAvatarUrl());
            profile.setBio(updatedProfile.getBio());
            profile.setExperienceDetail(updatedProfile.getExperienceDetail());
            profile.setYearsOfExperience(updatedProfile.getYearsOfExperience());
            profile.setFacebookUrl(updatedProfile.getFacebookUrl());
            profile.setInstagramUrl(updatedProfile.getInstagramUrl());
            profile.setTiktokUrl(updatedProfile.getTiktokUrl());
            if (updatedProfile.getIsDisplayed() != null) {
                profile.setIsDisplayed(updatedProfile.getIsDisplayed());
            }
        }

        // Update User fields (fullName, email, phone)
        if (updatedProfile.getFullName() != null && !updatedProfile.getFullName().isBlank()) {
            user.setFullName(updatedProfile.getFullName());
        }
        if (updatedProfile.getEmail() != null && !updatedProfile.getEmail().isBlank()) {
            user.setEmail(updatedProfile.getEmail());
        }
        user.setPhone(updatedProfile.getPhone());

        // Update Role field (exclude ADMIN user to prevent lockout)
        if (updatedProfile.getRoleId() != null && !"ADMIN".equalsIgnoreCase(user.getRole().getRoleName())) {
            Role role = roleRepository.findById(updatedProfile.getRoleId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò với ID: " + updatedProfile.getRoleId()));
            user.setRole(role);
        }

        userRepository.save(user);
        StaffProfile saved = staffProfileRepository.save(profile);
        User u = saved.getUser();
        return StaffProfileResponse.builder()
                .profileId(saved.getId())
                .userId(u.getId())
                .username(u.getUsername())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .roleName(u.getRole().getRoleName())
                .avatarUrl(saved.getAvatarUrl())
                .bio(saved.getBio())
                .experienceDetail(saved.getExperienceDetail())
                .yearsOfExperience(saved.getYearsOfExperience())
                .facebookUrl(saved.getFacebookUrl())
                .instagramUrl(saved.getInstagramUrl())
                .tiktokUrl(saved.getTiktokUrl())
                .isActive(u.getIsActive())
                .isDisplayed(saved.getIsDisplayed())
                .build();
    }

    @Override
    @Transactional
    public void toggleStaffActive(Long id) {
        StaffProfile profile = staffProfileRepository.findById(id).orElse(null);
        User user;
        if (profile != null) {
            user = profile.getUser();
        } else {
            user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản hoặc hồ sơ nhân viên với ID: " + id));
        }
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void toggleStaffDisplay(Long id) {
        StaffProfile profile = staffProfileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ nhân viên với ID: " + id));
        profile.setIsDisplayed(!profile.getIsDisplayed());
        staffProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public void resetStaffPassword(Long id, String newPassword) {
        StaffProfile profile = staffProfileRepository.findById(id).orElse(null);
        User user;
        if (profile != null) {
            user = profile.getUser();
        } else {
            user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản hoặc hồ sơ nhân viên với ID: " + id));
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public List<CustomerSummaryResponse> getCustomers(String search, int page, int size) {
        List<Object[]> customerData = bookingRepository.findUniqueCustomers();
        List<CustomerSummaryResponse> result = new ArrayList<>();

        for (Object[] row : customerData) {
            String name = (String) row[0];
            String phone = (String) row[1];
            String email = (String) row[2];

            if (search != null && !search.isBlank()) {
                if (!name.toLowerCase().contains(search.toLowerCase()) && !phone.contains(search)) {
                    continue;
                }
            }

            List<Booking> bookings = bookingRepository.findByCustomerPhone(phone);
            BigDecimal totalSpent = bookings.stream()
                    .filter(b -> b.getPaymentStatus() == PaymentStatus.PAID)
                    .map(Booking::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<CustomerSummaryResponse.CustomerBookingItem> history = bookings.stream()
                    .map(b -> CustomerSummaryResponse.CustomerBookingItem.builder()
                            .bookingCode(b.getBookingCode())
                            .shootDate(b.getShootDate())
                            .status(b.getBookingStatus())
                            .totalAmount(b.getTotalAmount())
                            .build())
                    .collect(Collectors.toList());

            result.add(CustomerSummaryResponse.builder()
                    .customerName(name)
                    .customerPhone(phone)
                    .customerEmail(email)
                    .totalBookings(bookings.size())
                    .totalSpent(totalSpent)
                    .bookingHistory(history)
                    .build());
        }

        return result.stream()
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    // =========================================================
    // V. CMS NỘI DUNG (CATEGORIES, CONCEPTS, BLOGS, CONFIG)
    // =========================================================

    @Override
    public List<ServicePackage> getAllPackages(int page, int size) {
        return servicePackageRepository.findAll().stream()
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public ServicePackage getPackageById(Long id) {
        return servicePackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy gói dịch vụ với ID: " + id));
    }

    @Override
    @Transactional
    public ServicePackage createPackage(ServicePackage pkg) {
        if (pkg.getSlug() == null || pkg.getSlug().isBlank()) {
            pkg.setSlug(toSlug(pkg.getPackageName()) + "-" + (System.currentTimeMillis() % 10000));
        }
        return servicePackageRepository.save(pkg);
    }

    @Override
    @Transactional
    public ServicePackage updatePackage(Long id, ServicePackage updated) {
        ServicePackage pkg = servicePackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy gói dịch vụ với ID: " + id));

        pkg.setPackageName(updated.getPackageName());
        pkg.setPrice(updated.getPrice());
        pkg.setShortDescription(updated.getShortDescription());
        pkg.setDetailContent(updated.getDetailContent());
        pkg.setLayoutCount(updated.getLayoutCount());
        pkg.setOutfitCount(updated.getOutfitCount());
        pkg.setEditedPhotos(updated.getEditedPhotos());
        pkg.setMakeupPersonCount(updated.getMakeupPersonCount());
        pkg.setThumbnailUrl(updated.getThumbnailUrl());
        pkg.setIsActive(updated.getIsActive());

        if (updated.getSlug() != null && !updated.getSlug().isBlank()) {
            pkg.setSlug(updated.getSlug());
        } else if (pkg.getSlug() == null || pkg.getSlug().isBlank()) {
            pkg.setSlug(toSlug(updated.getPackageName()));
        }

        return servicePackageRepository.save(pkg);
    }

    @Override
    @Transactional
    public void deletePackage(Long id) {
        servicePackageRepository.deleteById(id);
    }

    @Override
    public List<Concept> getAllConcepts(int page, int size, ConceptType conceptType) {
        java.util.stream.Stream<Concept> stream = conceptRepository.findAll().stream();
        if (conceptType != null) {
            stream = stream.filter(c -> c.getConceptType() == conceptType);
        }
        return stream
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public Concept getConceptById(Long id) {
        return conceptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Concept với ID: " + id));
    }

    @Override
    @Transactional
    public Concept createConcept(Concept concept) {
        if (concept.getSlug() == null || concept.getSlug().isBlank()) {
            concept.setSlug(toSlug(concept.getTitle()) + "-" + (System.currentTimeMillis() % 10000));
        }
        return conceptRepository.save(concept);
    }

    @Override
    @Transactional
    public Concept updateConcept(Long id, Concept updated) {
        Concept concept = conceptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Concept với ID: " + id));

        concept.setTitle(updated.getTitle());
        concept.setDescription(updated.getDescription());
        concept.setThumbnailUrl(updated.getThumbnailUrl());
        concept.setConceptType(updated.getConceptType());
        concept.setStatus(updated.getStatus());

        if (updated.getSlug() != null && !updated.getSlug().isBlank()) {
            concept.setSlug(updated.getSlug());
        } else if (concept.getSlug() == null || concept.getSlug().isBlank()) {
            concept.setSlug(toSlug(updated.getTitle()));
        }

        return conceptRepository.save(concept);
    }

    @Override
    @Transactional
    public void deleteConcept(Long id) {
        conceptRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ConceptImage addConceptImage(Long conceptId, org.springframework.web.multipart.MultipartFile file, String imageUrl, int sortOrder) {
        Concept concept = conceptRepository.findById(conceptId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Concept với ID: " + conceptId));

        String finalImageUrl = imageUrl;
        if (file != null && !file.isEmpty()) {
            finalImageUrl = cloudinaryService.uploadFile(file, "concepts");
        }

        if (finalImageUrl == null || finalImageUrl.isBlank()) {
            throw new IllegalArgumentException("Vui lòng cung cấp tệp tin ảnh hoặc đường dẫn ảnh URL!");
        }

        ConceptImage image = ConceptImage.builder()
                .concept(concept)
                .imageUrl(finalImageUrl)
                .sortOrder(sortOrder)
                .build();

        return conceptImageRepository.save(image);
    }

    @Override
    @Transactional
    public void deleteConceptImage(Long imageId) {
        conceptImageRepository.deleteById(imageId);
    }

    @Override
    @Transactional
    public void reorderConceptImages(List<Long> imageIds) {
        for (int i = 0; i < imageIds.size(); i++) {
            Long imageId = imageIds.get(i);
            ConceptImage image = conceptImageRepository.findById(imageId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hình ảnh Concept với ID: " + imageId));
            image.setSortOrder(i + 1);
            conceptImageRepository.save(image);
        }
    }

    @Override
    public List<Blog> getAllBlogs(int page, int size) {
        return blogRepository.findAll().stream()
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public Blog getBlogById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết Blog với ID: " + id));
    }

    @Override
    @Transactional
    public Blog createBlog(Blog blog, Long conceptId) {
        if (blog.getSlug() == null || blog.getSlug().isBlank()) {
            blog.setSlug(toSlug(blog.getTitle()) + "-" + (System.currentTimeMillis() % 10000));
        }
        if (conceptId != null) {
            Concept concept = conceptRepository.findById(conceptId).orElse(null);
            blog.setConcept(concept);
        }
        return blogRepository.save(blog);
    }

    @Override
    @Transactional
    public Blog updateBlog(Long id, Blog updated, Long conceptId) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết Blog với ID: " + id));

        blog.setTitle(updated.getTitle());
        blog.setThumbnailUrl(updated.getThumbnailUrl());
        blog.setContent(updated.getContent());
        blog.setStatus(updated.getStatus());

        if (updated.getSlug() != null && !updated.getSlug().isBlank()) {
            blog.setSlug(updated.getSlug());
        } else if (blog.getSlug() == null || blog.getSlug().isBlank()) {
            blog.setSlug(toSlug(updated.getTitle()));
        }

        if (conceptId != null) {
            Concept concept = conceptRepository.findById(conceptId).orElse(null);
            blog.setConcept(concept);
        } else {
            blog.setConcept(null);
        }

        return blogRepository.save(blog);
    }

    @Override
    @Transactional
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CustomerStory createStory(CustomerStory story) {
        return customerStoryRepository.save(story);
    }

    @Override
    @Transactional
    public CustomerStory updateStory(Long id, CustomerStory updated) {
        CustomerStory story = customerStoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy câu chuyện khách hàng với ID: " + id));

        story.setCustomerName(updated.getCustomerName());
        story.setAvatarUrl(updated.getAvatarUrl());
        story.setImageAfterUrl(updated.getImageAfterUrl());
        story.setStoryContent(updated.getStoryContent());
        story.setIsDisplayed(updated.getIsDisplayed());

        return customerStoryRepository.save(story);
    }

    @Override
    @Transactional
    public void deleteStory(Long id) {
        customerStoryRepository.deleteById(id);
    }

    @Override
    public StudioInformation getStudioInfo() {
        return studioInformationRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Thông tin Studio chưa được cấu hình."));
    }

    @Override
    @Transactional
    public StudioInformation updateStudioInfo(StudioInformation updated) {
        StudioInformation info = studioInformationRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Thông tin Studio chưa được cấu hình."));

        info.setStudioName(updated.getStudioName());
        info.setLogoUrl(updated.getLogoUrl());
        info.setBannerUrl(updated.getBannerUrl());
        info.setAddress(updated.getAddress());
        info.setPhone(updated.getPhone());
        info.setEmail(updated.getEmail());
        info.setFacebookUrl(updated.getFacebookUrl());
        info.setZaloUrl(updated.getZaloUrl());
        info.setYoutubeUrl(updated.getYoutubeUrl());
        info.setInstagramUrl(updated.getInstagramUrl());
        info.setTiktokUrl(updated.getTiktokUrl());
        info.setIntroVideoUrl(updated.getIntroVideoUrl());
        info.setIntroduction(updated.getIntroduction());
        info.setWorkingProcess(updated.getWorkingProcess());
        info.setGoogleMapUrl(updated.getGoogleMapUrl());

        return studioInformationRepository.save(info);
    }

    // =========================================================
    // PRIVATE HELPERS
    // =========================================================

    private User getAuthenticatedUser() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            return null;
        }
        return userRepository.findByUsername(username)
                .orElse(null); // Fallback if called outside authentication context
    }

    private String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String temp = input.toLowerCase()
            .replaceAll("[áàảãạăắằẳẵặâấầẩẫậ]", "a")
            .replaceAll("[éèẻẽẹêếềểễệ]", "e")
            .replaceAll("[íìỉĩị]", "i")
            .replaceAll("[óòỏõọôốồổỗộơớờởỡợ]", "o")
            .replaceAll("[úùủũụưứừửữự]", "u")
            .replaceAll("[ýỳỷỹỵ]", "y")
            .replaceAll("đ", "d")
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-")
            .replaceAll("-+", "-")
            .trim();
        if (temp.startsWith("-")) temp = temp.substring(1);
        if (temp.endsWith("-")) temp = temp.substring(0, temp.length() - 1);
        return temp;
    }

    @Override
    @Transactional
    public StaffProfileResponse createStaff(StaffCreateRequest request, org.springframework.web.multipart.MultipartFile avatarFile) {
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String url = cloudinaryService.uploadFile(avatarFile, "avatars");
            request.setAvatarUrl(url);
        }
        return createStaff(request);
    }

    @Override
    @Transactional
    public StaffProfileResponse updateStaff(Long id, StaffUpdateRequest profile, org.springframework.web.multipart.MultipartFile avatarFile) {
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String url = cloudinaryService.uploadFile(avatarFile, "avatars");
            profile.setAvatarUrl(url);
        }
        return updateStaff(id, profile);
    }

    @Override
    @Transactional
    public ServicePackage createPackage(ServicePackage pkg, org.springframework.web.multipart.MultipartFile thumbnailFile) {
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String url = cloudinaryService.uploadFile(thumbnailFile, "packages");
            pkg.setThumbnailUrl(url);
        }
        return createPackage(pkg);
    }

    @Override
    @Transactional
    public ServicePackage updatePackage(Long id, ServicePackage pkg, org.springframework.web.multipart.MultipartFile thumbnailFile) {
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String url = cloudinaryService.uploadFile(thumbnailFile, "packages");
            pkg.setThumbnailUrl(url);
        }
        return updatePackage(id, pkg);
    }

    @Override
    @Transactional
    public Concept createConcept(Concept concept, org.springframework.web.multipart.MultipartFile thumbnailFile) {
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String url = cloudinaryService.uploadFile(thumbnailFile, "concepts");
            concept.setThumbnailUrl(url);
        }
        return createConcept(concept);
    }

    @Override
    @Transactional
    public Concept updateConcept(Long id, Concept concept, org.springframework.web.multipart.MultipartFile thumbnailFile) {
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String url = cloudinaryService.uploadFile(thumbnailFile, "concepts");
            concept.setThumbnailUrl(url);
        }
        return updateConcept(id, concept);
    }

    @Override
    @Transactional
    public Blog createBlog(Blog blog, Long conceptId, org.springframework.web.multipart.MultipartFile thumbnailFile) {
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String url = cloudinaryService.uploadFile(thumbnailFile, "blogs");
            blog.setThumbnailUrl(url);
        }
        return createBlog(blog, conceptId);
    }

    @Override
    @Transactional
    public Blog updateBlog(Long id, Blog blog, Long conceptId, org.springframework.web.multipart.MultipartFile thumbnailFile) {
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String url = cloudinaryService.uploadFile(thumbnailFile, "blogs");
            blog.setThumbnailUrl(url);
        }
        return updateBlog(id, blog, conceptId);
    }

    @Override
    @Transactional
    public CustomerStory createStory(CustomerStory story, org.springframework.web.multipart.MultipartFile avatarFile, org.springframework.web.multipart.MultipartFile imageAfterFile) {
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String url = cloudinaryService.uploadFile(avatarFile, "stories");
            story.setAvatarUrl(url);
        }
        if (imageAfterFile != null && !imageAfterFile.isEmpty()) {
            String url = cloudinaryService.uploadFile(imageAfterFile, "stories");
            story.setImageAfterUrl(url);
        }
        return createStory(story);
    }

    @Override
    @Transactional
    public CustomerStory updateStory(Long id, CustomerStory story, org.springframework.web.multipart.MultipartFile avatarFile, org.springframework.web.multipart.MultipartFile imageAfterFile) {
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String url = cloudinaryService.uploadFile(avatarFile, "stories");
            story.setAvatarUrl(url);
        }
        if (imageAfterFile != null && !imageAfterFile.isEmpty()) {
            String url = cloudinaryService.uploadFile(imageAfterFile, "stories");
            story.setImageAfterUrl(url);
        }
        return updateStory(id, story);
    }

    @Override
    @Transactional
    public StudioInformation updateStudioInfo(StudioInformation info, org.springframework.web.multipart.MultipartFile logoFile, org.springframework.web.multipart.MultipartFile bannerFile) {
        if (logoFile != null && !logoFile.isEmpty()) {
            String url = cloudinaryService.uploadFile(logoFile, "studio");
            info.setLogoUrl(url);
        }
        if (bannerFile != null && !bannerFile.isEmpty()) {
            String url = cloudinaryService.uploadFile(bannerFile, "studio");
            info.setBannerUrl(url);
        }
        return updateStudioInfo(info);
    }

    private AdminBookingResponse toAdminBookingResponse(Booking booking) {
        List<AdminBookingResponse.AssignedStaffItem> assignedStaff = bookingAssignmentRepository.findByBookingId(booking.getId()).stream()
                .map(assignment -> AdminBookingResponse.AssignedStaffItem.builder()
                        .staffId(assignment.getStaff().getId())
                        .fullName(assignment.getStaff().getFullName())
                        .role(assignment.getStaff().getRole().getRoleName())
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
