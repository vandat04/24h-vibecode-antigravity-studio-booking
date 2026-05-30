package com.studio.dto.response;

import com.studio.constant.ConceptType;
import com.studio.constant.PublishStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ConceptDetailResponse {
    private Long id;
    private String title;
    private String slug;
    private ConceptType conceptType;
    private String thumbnailUrl;
    private String description;
    private PublishStatus status;
    private LocalDateTime createdAt;
    private List<ConceptImageItem> images;
    private List<CreditItem> credits;

    @Getter
    @Builder
    public static class ConceptImageItem {
        private Long id;
        private String imageUrl;
        private Integer sortOrder;
    }

    @Getter
    @Builder
    public static class CreditItem {
        private String fullName;
        private String role;
    }
}
