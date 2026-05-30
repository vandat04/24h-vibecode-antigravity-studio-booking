package com.studio.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffCreateRequest {

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6 ký tự trở lên")
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100)
    private String email;

    @Size(max = 15)
    private String phone;

    @NotNull(message = "Vui lòng chọn vai trò nhân viên (2: MAKEUP, 3: PHOTOGRAPHER)")
    private Integer roleId;

    // Các trường thông tin hồ sơ (Staff Profile) công khai
    private String avatarUrl;

    private String bio;

    private String experienceDetail;

    @Min(value = 0, message = "Số năm kinh nghiệm không hợp lệ")
    private Integer yearsOfExperience;

    private String facebookUrl;
    private String instagramUrl;
    private String tiktokUrl;

    private Boolean isDisplayed = true;
}
