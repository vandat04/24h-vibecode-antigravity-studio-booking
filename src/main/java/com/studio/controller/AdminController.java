package com.studio.controller;

import com.studio.constant.*;
import com.studio.dto.request.*;
import com.studio.dto.response.*;
import com.studio.entity.*;
import com.studio.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // =========================================================
    // I. QUẢN LÝ LỊCH HẸN & VÒNG ĐỜI ĐƠN ĐẶT (BOOKINGS)
    // =========================================================
    @GetMapping("/bookings")
    public ResponseEntity<List<AdminBookingResponse>> getBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) BookingStatus status) {
        return ResponseEntity.ok(adminService.getBookings(page, size, status));
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<AdminBookingResponse> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getBookingById(id));
    }

    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<AdminBookingResponse> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status,
            @RequestParam(required = false) String note) {
        return ResponseEntity.ok(adminService.updateBookingStatus(id, status, note));
    }

    @PutMapping("/bookings/{id}/payment")
    public ResponseEntity<AdminBookingResponse> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus status,
            @RequestParam(required = false, defaultValue = "BANK") String method) {
        return ResponseEntity.ok(adminService.updatePaymentStatus(id, status, method));
    }

    @PostMapping("/bookings/{id}/assign")
    public ResponseEntity<AdminBookingResponse> assignStaff(
            @PathVariable Long id,
            @RequestParam Long photographerId,
            @RequestParam Long makeupId) {
        return ResponseEntity.ok(adminService.assignStaff(id, photographerId, makeupId));
    }

    @GetMapping("/bookings/{id}/history")
    public ResponseEntity<List<BookingStatusHistory>> getBookingHistory(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getBookingHistory(id));
    }

    // =========================================================
    // II. GIÁM SÁT HẬU KỲ (POST-PRODUCTION)
    // =========================================================
    @GetMapping("/post-productions")
    public ResponseEntity<List<PostProductionHistory>> getPostProductions(
            @RequestParam(required = false) ProductionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getPostProductions(status, page, size));
    }

    @PutMapping("/bookings/{bookingId}/post-production")
    public ResponseEntity<PostProductionHistory> updatePostProduction(
            @PathVariable Long bookingId,
            @Valid @RequestBody PostProductionUpdateRequest request) {
        return ResponseEntity.ok(adminService.updatePostProduction(bookingId, request));
    }

    // =========================================================
    // III. BÁO CÁO & THỐNG KÊ (DASHBOARD)
    // =========================================================
    @GetMapping("/dashboard/revenue")
    public ResponseEntity<RevenueResponse> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(adminService.getRevenueReport(startDate, endDate));
    }

    @GetMapping("/dashboard/statistics")
    public ResponseEntity<Map<String, Object>> getDashboardStatistics() {
        return ResponseEntity.ok(adminService.getDashboardStatistics());
    }

    // =========================================================
    // IV. QUẢN LÝ NHÂN SỰ & KHÁCH HÀNG (STAFF & CUSTOMERS)
    // =========================================================
    @GetMapping("/staff")
    public ResponseEntity<List<StaffProfileResponse>> getAllStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role) {
        return ResponseEntity.ok(adminService.getAllStaff(page, size, role));
    }

    @PostMapping(value = "/staff", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StaffProfileResponse> createStaff(@Valid @RequestBody StaffCreateRequest request) {
        return ResponseEntity.ok(adminService.createStaff(request));
    }

    @PostMapping(value = "/staff", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StaffProfileResponse> createStaffMultipart(
            @RequestPart("staff") @Valid StaffCreateRequest request,
            @RequestPart(value = "avatarFile", required = false) MultipartFile avatarFile) {
        return ResponseEntity.ok(adminService.createStaff(request, avatarFile));
    }

    @PutMapping(value = "/staff/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StaffProfileResponse> updateStaff(@PathVariable Long id, @Valid @RequestBody StaffUpdateRequest profile) {
        return ResponseEntity.ok(adminService.updateStaff(id, profile));
    }

    @PutMapping(value = "/staff/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StaffProfileResponse> updateStaffMultipart(
            @PathVariable Long id,
            @RequestPart("profile") @Valid StaffUpdateRequest profile,
            @RequestPart(value = "avatarFile", required = false) MultipartFile avatarFile) {
        return ResponseEntity.ok(adminService.updateStaff(id, profile, avatarFile));
    }

    @PutMapping("/staff/{id}/toggle-active")
    public ResponseEntity<Map<String, String>> toggleStaffActive(@PathVariable Long id) {
        adminService.toggleStaffActive(id);
        return ResponseEntity.ok(Map.of("message", "Thay đổi trạng thái hoạt động tài khoản thành công!"));
    }

    @PutMapping("/staff/{id}/toggle-display")
    public ResponseEntity<Map<String, String>> toggleStaffDisplay(@PathVariable Long id) {
        adminService.toggleStaffDisplay(id);
        return ResponseEntity.ok(Map.of("message", "Thay đổi trạng thái hiển thị hồ sơ thành công!"));
    }

    @PostMapping("/staff/{id}/reset-password")
    public ResponseEntity<Map<String, String>> resetStaffPassword(
            @PathVariable Long id,
            @RequestParam String newPassword) {
        adminService.resetStaffPassword(id, newPassword);
        return ResponseEntity.ok(Map.of("message", "Đặt lại mật khẩu thành công!"));
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerSummaryResponse>> getCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getCustomers(search, page, size));
    }

    // =========================================================
    // V. CMS NỘI DUNG & CẤU HÌNH HỆ THỐNG (CMS)
    // =========================================================
    @GetMapping("/packages")
    public ResponseEntity<List<ServicePackage>> getAllPackages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getAllPackages(page, size));
    }

    @GetMapping("/packages/{id}")
    public ResponseEntity<ServicePackage> getPackageById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getPackageById(id));
    }
    @PostMapping(value = "/packages", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServicePackage> createPackage(@Valid @RequestBody ServicePackage pkg) {
        return ResponseEntity.ok(adminService.createPackage(pkg));
    }

    @PostMapping(value = "/packages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ServicePackage> createPackageMultipart(
            @RequestPart("package") @Valid ServicePackage pkg,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile) {
        return ResponseEntity.ok(adminService.createPackage(pkg, thumbnailFile));
    }

    @PutMapping(value = "/packages/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServicePackage> updatePackage(@PathVariable Long id, @Valid @RequestBody ServicePackage pkg) {
        return ResponseEntity.ok(adminService.updatePackage(id, pkg));
    }

    @PutMapping(value = "/packages/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ServicePackage> updatePackageMultipart(
            @PathVariable Long id,
            @RequestPart("package") @Valid ServicePackage pkg,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile) {
        return ResponseEntity.ok(adminService.updatePackage(id, pkg, thumbnailFile));
    }

    @DeleteMapping("/packages/{id}")
    public ResponseEntity<Map<String, String>> deletePackage(@PathVariable Long id) {
        adminService.deletePackage(id);
        return ResponseEntity.ok(Map.of("message", "Xóa gói dịch vụ thành công!"));
    }

    @GetMapping("/concepts")
    public ResponseEntity<List<Concept>> getAllConcepts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ConceptType conceptType) {
        return ResponseEntity.ok(adminService.getAllConcepts(page, size, conceptType));
    }

    @GetMapping("/concepts/{id}")
    public ResponseEntity<Concept> getConceptById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getConceptById(id));
    }

    @PostMapping(value = "/concepts", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Concept> createConcept(@Valid @RequestBody Concept concept) {
        return ResponseEntity.ok(adminService.createConcept(concept));
    }

    @PostMapping(value = "/concepts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Concept> createConceptMultipart(
            @RequestPart("concept") @Valid Concept concept,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile) {
        return ResponseEntity.ok(adminService.createConcept(concept, thumbnailFile));
    }

    @PutMapping(value = "/concepts/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Concept> updateConcept(@PathVariable Long id, @Valid @RequestBody Concept concept) {
        return ResponseEntity.ok(adminService.updateConcept(id, concept));
    }

    @PutMapping(value = "/concepts/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Concept> updateConceptMultipart(
            @PathVariable Long id,
            @RequestPart("concept") @Valid Concept concept,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile) {
        return ResponseEntity.ok(adminService.updateConcept(id, concept, thumbnailFile));
    }

    @DeleteMapping("/concepts/{id}")
    public ResponseEntity<Map<String, String>> deleteConcept(@PathVariable Long id) {
        adminService.deleteConcept(id);
        return ResponseEntity.ok(Map.of("message", "Xóa Concept thành công!"));
    }

    @PostMapping("/concepts/{id}/images")
    public ResponseEntity<ConceptImage> addConceptImage(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) org.springframework.web.multipart.MultipartFile file,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(defaultValue = "0") int sortOrder) {
        return ResponseEntity.ok(adminService.addConceptImage(id, file, imageUrl, sortOrder));
    }

    @DeleteMapping("/concepts/images/{imageId}")
    public ResponseEntity<Map<String, String>> deleteConceptImage(@PathVariable Long imageId) {
        adminService.deleteConceptImage(imageId);
        return ResponseEntity.ok(Map.of("message", "Xóa ảnh Concept thành công!"));
    }

    @PutMapping("/concepts/images/sort")
    public ResponseEntity<Map<String, String>> reorderConceptImages(@RequestBody List<Long> imageIds) {
        adminService.reorderConceptImages(imageIds);
        return ResponseEntity.ok(Map.of("message", "Sắp xếp lại thứ tự ảnh Concept thành công!"));
    }

    @GetMapping("/blogs")
    public ResponseEntity<List<Blog>> getAllBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getAllBlogs(page, size));
    }

    @GetMapping("/blogs/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getBlogById(id));
    }

    @PostMapping(value = "/blogs", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Blog> createBlog(@Valid @RequestBody Blog blog, @RequestParam(required = false) Long conceptId) {
        return ResponseEntity.ok(adminService.createBlog(blog, conceptId));
    }

    @PostMapping(value = "/blogs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Blog> createBlogMultipart(
            @RequestPart("blog") @Valid Blog blog,
            @RequestParam(required = false) Long conceptId,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile) {
        return ResponseEntity.ok(adminService.createBlog(blog, conceptId, thumbnailFile));
    }

    @PutMapping(value = "/blogs/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Blog> updateBlog(
            @PathVariable Long id,
            @Valid @RequestBody Blog blog,
            @RequestParam(required = false) Long conceptId) {
        return ResponseEntity.ok(adminService.updateBlog(id, blog, conceptId));
    }

    @PutMapping(value = "/blogs/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Blog> updateBlogMultipart(
            @PathVariable Long id,
            @RequestPart("blog") @Valid Blog blog,
            @RequestParam(required = false) Long conceptId,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile) {
        return ResponseEntity.ok(adminService.updateBlog(id, blog, conceptId, thumbnailFile));
    }

    @DeleteMapping("/blogs/{id}")
    public ResponseEntity<Map<String, String>> deleteBlog(@PathVariable Long id) {
        adminService.deleteBlog(id);
        return ResponseEntity.ok(Map.of("message", "Xóa bài viết Blog thành công!"));
    }

    @PostMapping(value = "/stories", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerStory> createStory(@Valid @RequestBody CustomerStory story) {
        return ResponseEntity.ok(adminService.createStory(story));
    }

    @PostMapping(value = "/stories", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerStory> createStoryMultipart(
            @RequestPart("story") @Valid CustomerStory story,
            @RequestPart(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestPart(value = "imageAfterFile", required = false) MultipartFile imageAfterFile) {
        return ResponseEntity.ok(adminService.createStory(story, avatarFile, imageAfterFile));
    }

    @PutMapping(value = "/stories/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerStory> updateStory(@PathVariable Long id, @Valid @RequestBody CustomerStory story) {
        return ResponseEntity.ok(adminService.updateStory(id, story));
    }

    @PutMapping(value = "/stories/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerStory> updateStoryMultipart(
            @PathVariable Long id,
            @RequestPart("story") @Valid CustomerStory story,
            @RequestPart(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestPart(value = "imageAfterFile", required = false) MultipartFile imageAfterFile) {
        return ResponseEntity.ok(adminService.updateStory(id, story, avatarFile, imageAfterFile));
    }

    @DeleteMapping("/stories/{id}")
    public ResponseEntity<Map<String, String>> deleteStory(@PathVariable Long id) {
        adminService.deleteStory(id);
        return ResponseEntity.ok(Map.of("message", "Xóa câu chuyện khách hàng thành công!"));
    }

    @GetMapping("/info")
    public ResponseEntity<StudioInformation> getStudioInfo() {
        return ResponseEntity.ok(adminService.getStudioInfo());
    }

    @PutMapping(value = "/info", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StudioInformation> updateStudioInfo(@Valid @RequestBody StudioInformation info) {
        return ResponseEntity.ok(adminService.updateStudioInfo(info));
    }

    @PutMapping(value = "/info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudioInformation> updateStudioInfoMultipart(
            @RequestPart("info") @Valid StudioInformation info,
            @RequestPart(value = "logoFile", required = false) MultipartFile logoFile,
            @RequestPart(value = "bannerFile", required = false) MultipartFile bannerFile) {
        return ResponseEntity.ok(adminService.updateStudioInfo(info, logoFile, bannerFile));
    }
}
