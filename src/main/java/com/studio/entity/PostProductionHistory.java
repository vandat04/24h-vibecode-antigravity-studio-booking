package com.studio.entity;

import com.studio.constant.ProductionStatus;
import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_production_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PostProductionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, foreignKey = @ForeignKey(name = "fk_production_booking"))
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(name = "production_status", nullable = false)
    @Builder.Default
    private ProductionStatus productionStatus = ProductionStatus.UNPROCESSED;

    @Column(name = "raw_photo_link", length = 500)
    private String rawPhotoLink;

    @Column(name = "edited_photo_link", length = 500)
    private String editedPhotoLink;

    @Column(length = 255)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", nullable = false, foreignKey = @ForeignKey(name = "fk_production_user"))
    private User updatedBy;

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostProductionHistory that = (PostProductionHistory) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
