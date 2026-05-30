package com.studio.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CustomerStoryResponse {
    private Long id;
    private String customerName;
    // Before photo
    private String avatarUrl;
    // After photo
    private String imageAfterUrl;
    private String storyContent;
    private LocalDateTime createdAt;
}
