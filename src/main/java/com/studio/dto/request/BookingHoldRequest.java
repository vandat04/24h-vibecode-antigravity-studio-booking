package com.studio.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class BookingHoldRequest {

    @NotNull(message = "Vui lòng chọn ngày cần giữ chỗ")
    private LocalDate shootDate;

    @NotNull(message = "Vui lòng chọn khung giờ cần giữ chỗ")
    private LocalTime shootTimeSlot;

    @NotNull(message = "Vui lòng chọn Concept để khóa bối cảnh")
    private Long conceptId;

    @NotNull(message = "Vui lòng chọn gói dịch vụ để kiểm tra nhân sự")
    private Long packageId;
}
