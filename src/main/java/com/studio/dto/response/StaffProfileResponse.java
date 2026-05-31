package com.studio.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffProfileResponse {
    private Long profileId;
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String roleName;
    private String avatarUrl;
    private String bio;
    private String experienceDetail;
    private Integer yearsOfExperience;
    private String facebookUrl;
    private String instagramUrl;
    private String tiktokUrl;
    private Boolean isActive;
    private Boolean isDisplayed;
}
