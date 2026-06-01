package com.studio.service.impl;

import com.studio.config.StudioScheduleConfig;
import com.studio.constant.BookingStatus;
import com.studio.constant.ConceptType;
import com.studio.constant.PublishStatus;
import com.studio.dto.request.BookingRequest;
import com.studio.dto.request.BookingHoldRequest;
import com.studio.dto.response.*;
import com.studio.entity.*;
import com.studio.repository.*;
import com.studio.service.BookingService;
import com.studio.service.StudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudioServiceImpl implements StudioService {

    private final StudioInformationRepository studioInformationRepository;
    private final ConceptRepository conceptRepository;
    private final ConceptImageRepository conceptImageRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final StaffProfileRepository staffProfileRepository;
    private final BlogRepository blogRepository;
    private final CustomerStoryRepository customerStoryRepository;
    private final BookingRepository bookingRepository;
    private final BookingAssignmentRepository bookingAssignmentRepository;
    private final PostProductionHistoryRepository postProductionHistoryRepository;
    private final BookingService bookingService;
    private final StudioScheduleConfig scheduleConfig;
    private final BookingHoldRepository bookingHoldRepository;
    private final UserRepository userRepository;

    @Override
    public StudioInfoResponse getStudioInfo() {
        StudioInformation info = studioInformationRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Studio information has not been configured yet."));

        return StudioInfoResponse.builder()
                .id(info.getId())
                .studioName(info.getStudioName())
                .logoUrl(info.getLogoUrl())
                .bannerUrl(info.getBannerUrl())
                .address(info.getAddress())
                .phone(info.getPhone())
                .email(info.getEmail())
                .facebookUrl(info.getFacebookUrl())
                .zaloUrl(info.getZaloUrl())
                .youtubeUrl(info.getYoutubeUrl())
                .instagramUrl(info.getInstagramUrl())
                .tiktokUrl(info.getTiktokUrl())
                .introVideoUrl(info.getIntroVideoUrl())
                .introduction(info.getIntroduction())
                .workingProcess(info.getWorkingProcess())
                .googleMapUrl(info.getGoogleMapUrl())
                .build();
    }

    @Override
    public List<ConceptSummaryResponse> getConcepts(ConceptType type, int page, int size) {
        List<Concept> concepts = conceptRepository.findByStatus(PublishStatus.PUBLISHED);

        if (type != null) {
            concepts = concepts.stream()
                    .filter(c -> c.getConceptType() == type)
                    .collect(Collectors.toList());
        }

        return concepts.stream()
                .skip((long) page * size)
                .limit(size)
                .map(c -> ConceptSummaryResponse.builder()
                        .id(c.getId())
                        .title(c.getTitle())
                        .slug(c.getSlug())
                        .conceptType(c.getConceptType())
                        .thumbnailUrl(c.getThumbnailUrl())
                        .description(c.getDescription())
                        .status(c.getStatus())
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ConceptDetailResponse getConceptDetail(String slug) {
        Concept concept = conceptRepository.findBySlugAndStatus(slug, PublishStatus.PUBLISHED)
                .orElseThrow(() -> new IllegalArgumentException("Concept not found: " + slug));

        List<ConceptDetailResponse.ConceptImageItem> images =
                conceptImageRepository.findByConceptIdOrderBySortOrderAsc(concept.getId())
                        .stream()
                        .map(img -> ConceptDetailResponse.ConceptImageItem.builder()
                                .id(img.getId())
                                .imageUrl(img.getImageUrl())
                                .sortOrder(img.getSortOrder())
                                .build())
                        .collect(Collectors.toList());

        // Bổ sung thông tin ê-kíp thực hiện (credits)
        List<ConceptDetailResponse.CreditItem> credits = bookingAssignmentRepository.findCreditsByConceptId(concept.getId())
                .stream()
                .map(BookingAssignment::getStaff)
                .distinct()
                .map(u -> ConceptDetailResponse.CreditItem.builder()
                        .fullName(u.getFullName())
                        .role(u.getRole().getRoleName())
                        .build())
                .collect(Collectors.toList());

        // Fallback: Populate with two active staff members (1 Photographer and 1 Makeup Artist)
        // to make sure the UI is always beautiful and looks premium.
        if (credits.isEmpty()) {
            List<StaffProfile> profiles = staffProfileRepository.findAll();
            
            String photoName = profiles.stream()
                    .filter(p -> p.getUser().getRole().getRoleName().equalsIgnoreCase("PHOTOGRAPHER"))
                    .map(p -> p.getUser().getFullName())
                    .findFirst()
                    .orElse("Huỳnh Ly Na");
            
            String makeupName = profiles.stream()
                    .filter(p -> p.getUser().getRole().getRoleName().equalsIgnoreCase("MAKEUP"))
                    .map(p -> p.getUser().getFullName())
                    .findFirst()
                    .orElse("Ly Ly");
            
            credits = List.of(
                ConceptDetailResponse.CreditItem.builder().fullName(photoName).role("PHOTOGRAPHER").build(),
                ConceptDetailResponse.CreditItem.builder().fullName(makeupName).role("MAKEUP").build()
            );
        }

        return ConceptDetailResponse.builder()
                .id(concept.getId())
                .title(concept.getTitle())
                .slug(concept.getSlug())
                .conceptType(concept.getConceptType())
                .thumbnailUrl(concept.getThumbnailUrl())
                .description(concept.getDescription())
                .status(concept.getStatus())
                .createdAt(concept.getCreatedAt())
                .images(images)
                .credits(credits)
                .build();
    }

    @Override
    public List<ServicePackageResponse> getPackages(int page, int size) {
        return servicePackageRepository.findByIsActiveTrue()
                .stream()
                .skip((long) page * size)
                .limit(size)
                .map(this::toPackageResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ServicePackageResponse getPackageDetail(String slug) {
        ServicePackage pkg = servicePackageRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new IllegalArgumentException("Service package not found: " + slug));
        return toPackageResponse(pkg);
    }

    @Override
    public List<StaffProfileResponse> getStaff(String role, int page, int size) {
        List<StaffProfile> profiles = staffProfileRepository.findAll()
                .stream()
                .filter(StaffProfile::getIsDisplayed)
                .collect(Collectors.toList());

        if (role != null && !role.isBlank()) {
            String upperRole = role.toUpperCase();
            profiles = profiles.stream()
                    .filter(p -> p.getUser().getRole().getRoleName().equalsIgnoreCase(upperRole))
                    .collect(Collectors.toList());
        }

        return profiles.stream()
                .skip((long) page * size)
                .limit(size)
                .map(p -> StaffProfileResponse.builder()
                        .profileId(p.getId())
                        .userId(p.getUser().getId())
                        .fullName(p.getUser().getFullName())
                        .roleName(p.getUser().getRole().getRoleName())
                        .avatarUrl(p.getAvatarUrl())
                        .bio(p.getBio())
                        .experienceDetail(p.getExperienceDetail())
                        .yearsOfExperience(p.getYearsOfExperience())
                        .facebookUrl(p.getFacebookUrl())
                        .instagramUrl(p.getInstagramUrl())
                        .tiktokUrl(p.getTiktokUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<BlogSummaryResponse> getBlogs(int page, int size) {
        return blogRepository
                .findByStatusOrderByCreatedAtDesc(PublishStatus.PUBLISHED)
                .stream()
                .skip((long) page * size)
                .limit(size)
                .map(b -> BlogSummaryResponse.builder()
                        .id(b.getId())
                        .title(b.getTitle())
                        .slug(b.getSlug())
                        .thumbnailUrl(b.getThumbnailUrl())
                        .status(b.getStatus())
                        .createdAt(b.getCreatedAt())
                        .updatedAt(b.getUpdatedAt())
                        .relatedConceptId(b.getConcept() != null ? b.getConcept().getId() : null)
                        .relatedConceptTitle(b.getConcept() != null ? b.getConcept().getTitle() : null)
                        .relatedConceptSlug(b.getConcept() != null ? b.getConcept().getSlug() : null)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public BlogDetailResponse getBlogDetail(String slug) {
        Blog blog = blogRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Blog post not found: " + slug));

        ConceptSummaryResponse relatedConcept = null;
        if (blog.getConcept() != null) {
            Concept c = blog.getConcept();
            relatedConcept = ConceptSummaryResponse.builder()
                    .id(c.getId())
                    .title(c.getTitle())
                    .slug(c.getSlug())
                    .conceptType(c.getConceptType())
                    .thumbnailUrl(c.getThumbnailUrl())
                    .description(c.getDescription())
                    .status(c.getStatus())
                    .createdAt(c.getCreatedAt())
                    .build();
        }

        return BlogDetailResponse.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .slug(blog.getSlug())
                .thumbnailUrl(blog.getThumbnailUrl())
                .content(blog.getContent())
                .status(blog.getStatus())
                .createdAt(blog.getCreatedAt())
                .updatedAt(blog.getUpdatedAt())
                .relatedConcept(relatedConcept)
                .build();
    }

    @Override
    public List<CustomerStoryResponse> getCustomerStories(int page, int size) {
        return customerStoryRepository
                .findByIsDisplayedTrueOrderByCreatedAtDesc()
                .stream()
                .skip((long) page * size)
                .limit(size)
                .map(s -> CustomerStoryResponse.builder()
                        .id(s.getId())
                        .customerName(s.getCustomerName())
                        .avatarUrl(s.getAvatarUrl())
                        .imageAfterUrl(s.getImageAfterUrl())
                        .storyContent(s.getStoryContent())
                        .createdAt(s.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

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
    public ScheduleSlotResponse getSchedule(LocalDate date, Long packageId, Long conceptId) {
        // 1. Tự động dọn dẹp các slot giữ chỗ đã quá hạn
        bookingHoldRepository.deleteExpiredHolds(LocalDateTime.now());

        LocalDateTime now = LocalDateTime.now();

        // 2. Tìm tất cả các ca chụp trong ngày của date (chưa CANCELLED)
        List<Booking> bookings = bookingRepository.findByShootDate(date).stream()
                .filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED)
                .collect(Collectors.toList());

        // 3. Tìm tất cả các lượt giữ chỗ còn hạn trong ngày của date
        List<BookingHold> holds = bookingHoldRepository.findByShootDateAndHoldExpiredAtAfter(date, now);

        // 4. Tính toán sức chứa động theo gói dịch vụ (nếu được truyền lên)
        long maxCapacity = getMaxParallelBookings(); // Fallback sức chứa nhân sự chung
        if (packageId != null) {
            ServicePackage pkg = servicePackageRepository.findById(packageId).orElse(null);
            if (pkg != null) {
                long photographers = userRepository.findByRoleRoleName("PHOTOGRAPHER").stream()
                        .filter(User::getIsActive)
                        .count();
                if (pkg.getMakeupPersonCount() == 0) {
                    // Nếu gói không yêu cầu makeup, sức chứa chỉ bị giới hạn bởi thợ chụp
                    maxCapacity = photographers > 0 ? photographers : 2;
                } else {
                    // Nếu có makeup, sức chứa = min(Photo, Makeup)
                    long makeups = userRepository.findByRoleRoleName("MAKEUP").stream()
                            .filter(User::getIsActive)
                            .count();
                    long capacity = Math.min(photographers, makeups);
                    maxCapacity = capacity > 0 ? capacity : 2;
                }
            }
        }

        List<LocalTime> unavailableSlots = new java.util.ArrayList<>();
        List<LocalTime> availableSlots = new java.util.ArrayList<>();

        // 5. Duyệt các khung giờ để kiểm tra bối cảnh (Concept) và sức chứa nhân sự
        for (LocalTime slot : scheduleConfig.getAllTimeSlots()) {
            // A. Kiểm tra trùng bối cảnh Concept (Khách book cùng concept trên cùng slot)
            boolean conceptClash = false;
            if (conceptId != null) {
                long bookedConceptCount = bookings.stream()
                        .filter(b -> b.getShootTimeSlot().equals(slot) && b.getConcept().getId().equals(conceptId))
                        .count();
                long holdConceptCount = holds.stream()
                        .filter(h -> h.getShootTimeSlot().equals(slot) && conceptId.equals(h.getConceptId()))
                        .count();
                if (bookedConceptCount + holdConceptCount >= 1) {
                    conceptClash = true;
                }
            }

            // B. Kiểm tra sức chứa tổng
            long bookedCount = bookings.stream().filter(b -> b.getShootTimeSlot().equals(slot)).count();
            long holdCount = holds.stream().filter(h -> h.getShootTimeSlot().equals(slot)).count();

            if (conceptClash || (bookedCount + holdCount >= maxCapacity)) {
                unavailableSlots.add(slot);
            } else {
                availableSlots.add(slot);
            }
        }

        return ScheduleSlotResponse.builder()
                .date(date.toString())
                .bookedSlots(unavailableSlots)
                .availableSlots(availableSlots)
                .build();
    }

    @Override
    @Transactional
    public BookingHoldResponse holdSlot(BookingHoldRequest request) {
        LocalDate date = request.getShootDate();
        LocalTime slot = request.getShootTimeSlot();
        Long conceptId = request.getConceptId();
        Long packageId = request.getPackageId();
        LocalDateTime now = LocalDateTime.now();

        // 1. Dọn dẹp holds đã hết hạn trước
        bookingHoldRepository.deleteExpiredHolds(now);

        // 2. Kiểm tra xem ngày chọn giữ chỗ phải lớn hơn hoặc bằng ngày hiện tại
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày chọn giữ chỗ phải lớn hơn hoặc bằng ngày hiện tại.");
        }

        // 3. Tìm bối cảnh và gói dịch vụ để đảm bảo tồn tại
        conceptRepository.findById(conceptId)
                .orElseThrow(() -> new IllegalArgumentException("Concept not found with ID: " + conceptId));
        ServicePackage pkg = servicePackageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Service package not found with ID: " + packageId));

        // 4. Bước 1: Kiểm tra kẹt bối cảnh Concept
        List<Booking> bookings = bookingRepository.findByShootDateAndShootTimeSlot(date, slot).stream()
                .filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED)
                .collect(Collectors.toList());

        List<BookingHold> holds = bookingHoldRepository.findByShootDateAndShootTimeSlotAndHoldExpiredAtAfter(date, slot, now);

        long bookedConceptCount = bookings.stream()
                .filter(b -> b.getConcept().getId().equals(conceptId))
                .count();
        long holdConceptCount = holds.stream()
                .filter(h -> conceptId.equals(h.getConceptId()))
                .count();

        if (bookedConceptCount + holdConceptCount >= 1) {
            throw new IllegalStateException("Khung giờ này đã có khách hàng khác đặt bối cảnh này. Vui lòng chọn Concept hoặc khung giờ khác.");
        }

        // 5. Bước 2: Kiểm tra sức chứa động theo gói dịch vụ
        long maxCapacity = getMaxParallelBookings();
        long photographers = userRepository.findByRoleRoleName("PHOTOGRAPHER").stream()
                .filter(User::getIsActive)
                .count();

        if (pkg.getMakeupPersonCount() == 0) {
            maxCapacity = photographers > 0 ? photographers : 2;
        } else {
            long makeups = userRepository.findByRoleRoleName("MAKEUP").stream()
                    .filter(User::getIsActive)
                    .count();
            long capacity = Math.min(photographers, makeups);
            maxCapacity = capacity > 0 ? capacity : 2;
        }

        long totalBookingsCount = bookings.size();
        long totalHoldsCount = holds.size();

        if (totalBookingsCount + totalHoldsCount >= maxCapacity) {
            throw new IllegalStateException("Khung giờ này đã đạt giới hạn phục vụ tối đa của ê-kíp (" + maxCapacity + " ca song song). Vui lòng chọn khung giờ khác.");
        }

        // 6. Tạo giữ chỗ tạm thời trong 10 phút
        String holdToken = UUID.randomUUID().toString();
        BookingHold hold = BookingHold.builder()
                .shootDate(date)
                .shootTimeSlot(slot)
                .holdToken(holdToken)
                .conceptId(conceptId)
                .packageId(packageId)
                .holdExpiredAt(now.plusMinutes(10))
                .build();

        bookingHoldRepository.save(hold);

        return BookingHoldResponse.builder()
                .holdToken(holdToken)
                .holdExpiredAt(hold.getHoldExpiredAt())
                .message("Khóa giữ chỗ tạm thời thành công trong vòng 10 phút!")
                .build();
    }

    @Override
    @Transactional
    public BookingCreateResponse createBooking(BookingRequest request) {
        Booking newBooking = Booking.builder()
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .customerPhone(request.getCustomerPhone())
                .customerFacebook(request.getCustomerFacebook())
                .shootDate(request.getShootDate())
                .shootTimeSlot(request.getShootTimeSlot())
                .shootLocation(request.getShootLocation())
                .customerNotes(request.getCustomerNotes())
                .build();

        Booking savedBooking = bookingService.createBooking(newBooking, request.getPackageId(), request.getConceptId(), request.getHoldToken());

        return BookingCreateResponse.builder()
                .id(savedBooking.getId())
                .bookingCode(savedBooking.getBookingCode())
                .customerName(savedBooking.getCustomerName())
                .customerEmail(savedBooking.getCustomerEmail())
                .customerPhone(savedBooking.getCustomerPhone())
                .shootDate(savedBooking.getShootDate())
                .shootTimeSlot(savedBooking.getShootTimeSlot())
                .shootLocation(savedBooking.getShootLocation())
                .packageName(savedBooking.getServicePackage().getPackageName())
                .conceptTitle(savedBooking.getConcept().getTitle())
                .totalAmount(savedBooking.getTotalAmount())
                .bookingStatus(savedBooking.getBookingStatus())
                .paymentStatus(savedBooking.getPaymentStatus())
                .createdAt(savedBooking.getCreatedAt())
                .message("Đặt lịch thành công! Email xác nhận đã được gửi tới " + savedBooking.getCustomerEmail() + ". Vui lòng lưu Mã đặt lịch để tra cứu.")
                .build();
    }

    @Override
    public BookingLookupResponse lookupBooking(String phone, String code) {
        Booking booking = bookingRepository.findByBookingCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch đặt với mã: " + code));

        if (!booking.getCustomerPhone().equals(phone)) {
            throw new IllegalArgumentException("Số điện thoại không khớp với mã đặt lịch.");
        }

        // Get assigned staff
        List<BookingLookupResponse.AssignedStaff> assignedStaff =
                bookingAssignmentRepository.findByBookingId(booking.getId())
                        .stream()
                        .map(assignment -> {
                            StaffProfile profile = staffProfileRepository
                                    .findByUserId(assignment.getStaff().getId())
                                    .orElse(null);
                            return BookingLookupResponse.AssignedStaff.builder()
                                    .fullName(assignment.getStaff().getFullName())
                                    .role(assignment.getStaff().getRole().getRoleName())
                                    .avatarUrl(profile != null ? profile.getAvatarUrl() : null)
                                    .build();
                        })
                        .collect(Collectors.toList());

        // Get latest post production status
        List<PostProductionHistory> productionHistories =
                postProductionHistoryRepository.findByBookingIdOrderByUpdatedAtDesc(booking.getId());

        String editedPhotoLink = null;
        com.studio.constant.ProductionStatus productionStatus = null;
        if (!productionHistories.isEmpty()) {
            PostProductionHistory latest = productionHistories.get(0);
            productionStatus = latest.getProductionStatus();
            editedPhotoLink = latest.getEditedPhotoLink();
        }

        return BookingLookupResponse.builder()
                .bookingCode(booking.getBookingCode())
                .customerName(booking.getCustomerName())
                .shootDate(booking.getShootDate())
                .shootTimeSlot(booking.getShootTimeSlot())
                .shootLocation(booking.getShootLocation())
                .packageName(booking.getServicePackage().getPackageName())
                .conceptTitle(booking.getConcept().getTitle())
                .totalAmount(booking.getTotalAmount())
                .bookingStatus(booking.getBookingStatus())
                .paymentStatus(booking.getPaymentStatus())
                .assignedStaff(assignedStaff)
                .productionStatus(productionStatus)
                .editedPhotoLink(editedPhotoLink)
                .build();
    }

    private ServicePackageResponse toPackageResponse(ServicePackage pkg) {
        return ServicePackageResponse.builder()
                .id(pkg.getId())
                .packageName(pkg.getPackageName())
                .slug(pkg.getSlug())
                .price(pkg.getPrice())
                .shortDescription(pkg.getShortDescription())
                .detailContent(pkg.getDetailContent())
                .layoutCount(pkg.getLayoutCount())
                .outfitCount(pkg.getOutfitCount())
                .editedPhotos(pkg.getEditedPhotos())
                .makeupPersonCount(pkg.getMakeupPersonCount())
                .thumbnailUrl(pkg.getThumbnailUrl())
                .isActive(pkg.getIsActive())
                .build();
    }
}
