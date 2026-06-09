package com.studio.service;

import com.studio.constant.ConceptType;
import com.studio.dto.request.BookingRequest;
import com.studio.dto.response.*;
import com.studio.entity.ServiceType;
import java.time.LocalDate;
import java.util.List;

public interface StudioService {
    StudioInfoResponse getStudioInfo();
    List<ServiceType> getServiceTypes();
    List<ConceptSummaryResponse> getConcepts(ConceptType type, int page, int size);
    ConceptDetailResponse getConceptDetail(String slug);
    List<ServicePackageResponse> getPackages(Long serviceTypeId, int page, int size);
    ServicePackageResponse getPackageDetail(String slug);
    List<StaffProfileResponse> getStaff(String role, int page, int size);
    List<BlogSummaryResponse> getBlogs(int page, int size);
    BlogDetailResponse getBlogDetail(String slug);
    List<CustomerStoryResponse> getCustomerStories(int page, int size);
    ScheduleSlotResponse getSchedule(LocalDate date, Long packageId, Long conceptId);
    BookingHoldResponse holdSlot(com.studio.dto.request.BookingHoldRequest request);
    BookingCreateResponse createBooking(BookingRequest request);
    BookingLookupResponse lookupBooking(String phone, String code);
}
