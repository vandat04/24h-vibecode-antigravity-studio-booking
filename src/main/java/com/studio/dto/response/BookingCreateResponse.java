package com.studio.dto.response;

import com.studio.constant.BookingStatus;
import com.studio.constant.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class BookingCreateResponse {
    private Long id;
    private String bookingCode;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private LocalDate shootDate;
    private LocalTime shootTimeSlot;
    private String shootLocation;
    private String packageName;
    private String conceptTitle;
    private BigDecimal totalAmount;
    private BookingStatus bookingStatus;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    private String message;
}
