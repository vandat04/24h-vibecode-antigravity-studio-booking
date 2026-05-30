package com.studio.dto.response;

import com.studio.constant.PublishStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BlogSummaryResponse {
    private Long id;
    private String title;
    private String slug;
    private String thumbnailUrl;
    private PublishStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Related concept info (if available)
    private Long relatedConceptId;
    private String relatedConceptTitle;
    private String relatedConceptSlug;
}
