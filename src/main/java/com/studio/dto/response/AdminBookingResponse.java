package com.studio.dto.response;

import com.studio.constant.BookingStatus;
import com.studio.constant.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class AdminBookingResponse {
    private Long id;
    private String bookingCode;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerFacebook;
    private LocalDate shootDate;
    private LocalTime shootTimeSlot;
    private String shootLocation;
    private String customerNotes;
    private BigDecimal totalAmount;
    private BookingStatus bookingStatus;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    
    private String packageName;
    private String conceptTitle;

    private List<AssignedStaffItem> assignedStaff;

    @Getter
    @Builder
    public static class AssignedStaffItem {
        private Long staffId;
        private String fullName;
        private String role;
    }
}
