package com.studio.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ServicePackageResponse {
    private Long id;
    private String packageName;
    private String slug;
    private BigDecimal price;
    private String shortDescription;
    private String detailContent;
    private Integer layoutCount;
    private Integer outfitCount;
    private Integer editedPhotos;
    private Integer makeupPersonCount;
    private String thumbnailUrl;
    private Boolean isActive;
}
