package com.studio.dto.response;

import com.studio.constant.BookingStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class CustomerSummaryResponse {
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private long totalBookings;
    private BigDecimal totalSpent;
    private List<CustomerBookingItem> bookingHistory;

    @Getter
    @Builder
    public static class CustomerBookingItem {
        private String bookingCode;
        private LocalDate shootDate;
        private BookingStatus status;
        private BigDecimal totalAmount;
    }
}
