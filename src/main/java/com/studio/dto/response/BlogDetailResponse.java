package com.studio.dto.response;

import com.studio.constant.PublishStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BlogDetailResponse {
    private Long id;
    private String title;
    private String slug;
    private String thumbnailUrl;
    private String content;
    private PublishStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Related concept (to suggest the gallery)
    private ConceptSummaryResponse relatedConcept;
}
