package com.studio.service;

import com.studio.dto.request.ChangePasswordRequest;
import com.studio.dto.request.PostProductionUpdateRequest;
import com.studio.dto.response.AdminBookingResponse;
import com.studio.entity.PostProductionHistory;

import java.util.List;

public interface StaffService {
    List<AdminBookingResponse> getMyBookings(int page, int size);
    AdminBookingResponse getMyBookingDetail(Long bookingId);
    void confirmMakeupComplete(Long bookingId);
    PostProductionHistory updateMyPostProduction(Long bookingId, PostProductionUpdateRequest request);
    void changeMyPassword(ChangePasswordRequest request);
}
