package com.studio.entity;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@Entity
@Table(name = "service_packages", uniqueConstraints = {
    @UniqueConstraint(name = "uk_package_slug", columnNames = "slug")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ServicePackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @Column(name = "package_name", length = 150, nullable = false)
    private String packageName;

    @Column(length = 150, nullable = false)
    private String slug;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(name = "detail_content", columnDefinition = "TEXT", nullable = false)
    private String detailContent;

    @Column(name = "layout_count", columnDefinition = "TINYINT UNSIGNED", nullable = false)
    @Builder.Default
    private Integer layoutCount = 1;

    @Column(name = "outfit_count", columnDefinition = "TINYINT UNSIGNED", nullable = false)
    @Builder.Default
    private Integer outfitCount = 1;

    @Column(name = "edited_photos", columnDefinition = "TINYINT UNSIGNED", nullable = false)
    @Builder.Default
    private Integer editedPhotos = 0;

    @Column(name = "makeup_person_count", columnDefinition = "TINYINT UNSIGNED", nullable = false)
    @Builder.Default
    private Integer makeupPersonCount = 0;

    @Column(name = "thumbnail_url", length = 500, nullable = false)
    private String thumbnailUrl;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServicePackage that = (ServicePackage) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
