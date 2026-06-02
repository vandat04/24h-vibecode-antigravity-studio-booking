package com.studio.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffUpdateRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100)
    private String email;

    @Size(max = 15)
    private String phone;

    @NotNull(message = "Vui lòng chọn vai trò nhân viên")
    private Integer roleId;

    private String avatarUrl;
    private String bio;
    private String experienceDetail;

    @Min(value = 0, message = "Số năm kinh nghiệm không hợp lệ")
    private Integer yearsOfExperience;

    private String facebookUrl;
    private String instagramUrl;
    private String tiktokUrl;
    private Boolean isDisplayed;
}
