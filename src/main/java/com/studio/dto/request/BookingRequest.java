package com.studio.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class BookingRequest {

    @NotBlank(message = "Vui lòng nhập họ tên")
    @Size(max = 100, message = "Họ tên không được vượt quá 100 ký tự")
    private String customerName;

    @NotBlank(message = "Vui lòng nhập email")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100)
    private String customerEmail;

    @NotBlank(message = "Vui lòng nhập số điện thoại")
    @Pattern(regexp = "^(0|\\+84)\\d{9,10}$", message = "Số điện thoại không hợp lệ")
    private String customerPhone;

    @Size(max = 255)
    private String customerFacebook;

    @NotNull(message = "Vui lòng chọn ngày chụp")
    @FutureOrPresent(message = "Ngày chụp phải lớn hơn hoặc bằng ngày hiện tại")
    private LocalDate shootDate;

    @NotNull(message = "Vui lòng chọn khung giờ chụp")
    private LocalTime shootTimeSlot;

    @NotBlank(message = "Vui lòng nhập địa điểm chụp")
    @Size(max = 255)
    private String shootLocation;

    @NotNull(message = "Vui lòng chọn gói dịch vụ")
    private Long packageId;

    @NotNull(message = "Vui lòng chọn concept mong muốn")
    private Long conceptId;

    @Size(max = 1000, message = "Ghi chú không được vượt quá 1000 ký tự")
    private String customerNotes;

    private String holdToken;
}
