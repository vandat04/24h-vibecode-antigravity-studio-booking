package com.studio.dto.response;

import com.studio.constant.ConceptType;
import com.studio.constant.PublishStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ConceptSummaryResponse {
    private Long id;
    private String title;
    private String slug;
    private ConceptType conceptType;
    private String thumbnailUrl;
    private String description;
    private PublishStatus status;
    private LocalDateTime createdAt;
}
