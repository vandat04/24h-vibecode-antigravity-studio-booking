package com.studio.dto.response;

import com.studio.constant.BookingStatus;
import com.studio.constant.PaymentStatus;
import com.studio.constant.ProductionStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class BookingLookupResponse {
    private String bookingCode;
    private String customerName;
    private LocalDate shootDate;
    private LocalTime shootTimeSlot;
    private String shootLocation;
    private String packageName;
    private String conceptTitle;
    private BigDecimal totalAmount;
    private BookingStatus bookingStatus;
    private PaymentStatus paymentStatus;
    // Assigned staff list
    private List<AssignedStaff> assignedStaff;
    // Post production info (latest)
    private ProductionStatus productionStatus;
    private String editedPhotoLink;

    @Getter
    @Builder
    public static class AssignedStaff {
        private String fullName;
        private String role;
        private String avatarUrl;
    }
}
