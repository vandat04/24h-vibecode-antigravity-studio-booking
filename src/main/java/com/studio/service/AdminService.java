package com.studio.service;

import com.studio.constant.*;
import com.studio.dto.request.*;
import com.studio.dto.response.*;
import com.studio.entity.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AdminService {

    // I. Quản lý Lịch hẹn & Vòng đời Đơn đặt
    List<AdminBookingResponse> getBookings(int page, int size, BookingStatus status);
    AdminBookingResponse getBookingById(Long id);
    AdminBookingResponse updateBookingStatus(Long id, BookingStatus status, String note);
    AdminBookingResponse updatePaymentStatus(Long id, PaymentStatus status, String method);
    AdminBookingResponse assignStaff(Long id, Long photographerId, Long makeupId);
    List<BookingStatusHistory> getBookingHistory(Long id);

    // II. Giám sát & Quản lý Hậu kỳ
    List<PostProductionHistory> getPostProductions(ProductionStatus status);
    PostProductionHistory updatePostProduction(Long bookingId, PostProductionUpdateRequest request);

    // III. Báo cáo & Thống kê (Dashboard)
    RevenueResponse getRevenueReport(LocalDate startDate, LocalDate endDate);
    Map<String, Object> getDashboardStatistics();

    // IV. Quản lý Nhân sự & Khách hàng
    List<StaffProfileResponse> getAllStaff();
    StaffProfileResponse createStaff(StaffCreateRequest request);
    StaffProfileResponse createStaff(StaffCreateRequest request, org.springframework.web.multipart.MultipartFile avatarFile);
    StaffProfileResponse updateStaff(Long id, StaffProfile profile);
    StaffProfileResponse updateStaff(Long id, StaffProfile profile, org.springframework.web.multipart.MultipartFile avatarFile);
    void toggleStaffActive(Long id);
    void toggleStaffDisplay(Long id);
    void resetStaffPassword(Long id, String newPassword);
    List<CustomerSummaryResponse> getCustomers(String search);

    // V. CMS Nội dung & Gói chụp, Concept, Portfolio Images, Blogs, Stories, Cấu hình
    List<ServicePackage> getAllPackages();
    ServicePackage getPackageById(Long id);
    ServicePackage createPackage(ServicePackage pkg);
    ServicePackage createPackage(ServicePackage pkg, org.springframework.web.multipart.MultipartFile thumbnailFile);
    ServicePackage updatePackage(Long id, ServicePackage pkg);
    ServicePackage updatePackage(Long id, ServicePackage pkg, org.springframework.web.multipart.MultipartFile thumbnailFile);
    void deletePackage(Long id);

    List<Concept> getAllConcepts();
    Concept getConceptById(Long id);
    Concept createConcept(Concept concept);
    Concept createConcept(Concept concept, org.springframework.web.multipart.MultipartFile thumbnailFile);
    Concept updateConcept(Long id, Concept concept);
    Concept updateConcept(Long id, Concept concept, org.springframework.web.multipart.MultipartFile thumbnailFile);
    void deleteConcept(Long id);

    ConceptImage addConceptImage(Long conceptId, org.springframework.web.multipart.MultipartFile file, String imageUrl, int sortOrder);
    void deleteConceptImage(Long imageId);
    void reorderConceptImages(List<Long> imageIds);

    List<Blog> getAllBlogs();
    Blog getBlogById(Long id);
    Blog createBlog(Blog blog, Long conceptId);
    Blog createBlog(Blog blog, Long conceptId, org.springframework.web.multipart.MultipartFile thumbnailFile);
    Blog updateBlog(Long id, Blog blog, Long conceptId);
    Blog updateBlog(Long id, Blog blog, Long conceptId, org.springframework.web.multipart.MultipartFile thumbnailFile);
    void deleteBlog(Long id);

    CustomerStory createStory(CustomerStory story);
    CustomerStory createStory(CustomerStory story, org.springframework.web.multipart.MultipartFile avatarFile, org.springframework.web.multipart.MultipartFile imageAfterFile);
    CustomerStory updateStory(Long id, CustomerStory story);
    CustomerStory updateStory(Long id, CustomerStory story, org.springframework.web.multipart.MultipartFile avatarFile, org.springframework.web.multipart.MultipartFile imageAfterFile);
    void deleteStory(Long id);

    StudioInformation getStudioInfo();
    StudioInformation updateStudioInfo(StudioInformation info);
    StudioInformation updateStudioInfo(StudioInformation info, org.springframework.web.multipart.MultipartFile logoFile, org.springframework.web.multipart.MultipartFile bannerFile);
}
