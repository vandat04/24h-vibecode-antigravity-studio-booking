package com.studio.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String token;
    private String tokenType; // Sẽ mặc định là "Bearer"
    private String username;
    private String fullName;
    private String role;
    private String avatarUrl; // Dành cho hiển thị giao diện Frontend React
}
