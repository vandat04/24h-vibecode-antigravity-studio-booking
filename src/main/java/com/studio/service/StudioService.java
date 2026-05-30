package com.studio.service;

import com.studio.constant.ConceptType;
import com.studio.dto.request.BookingRequest;
import com.studio.dto.response.*;
import java.time.LocalDate;
import java.util.List;

public interface StudioService {
    StudioInfoResponse getStudioInfo();
    List<ConceptSummaryResponse> getConcepts(ConceptType type);
    ConceptDetailResponse getConceptDetail(String slug);
    List<ServicePackageResponse> getPackages();
    ServicePackageResponse getPackageDetail(String slug);
    List<StaffProfileResponse> getStaff(String role);
    List<BlogSummaryResponse> getBlogs();
    BlogDetailResponse getBlogDetail(String slug);
    List<CustomerStoryResponse> getCustomerStories();
    ScheduleSlotResponse getSchedule(LocalDate date, Long packageId, Long conceptId);
    BookingHoldResponse holdSlot(com.studio.dto.request.BookingHoldRequest request);
    BookingCreateResponse createBooking(BookingRequest request);
    BookingLookupResponse lookupBooking(String phone, String code);
}
