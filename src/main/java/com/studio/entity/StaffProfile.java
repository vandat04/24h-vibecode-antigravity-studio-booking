package com.studio.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "staff_profiles", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_profile", columnNames = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_profile_user"))
    private User user;

    @Column(name = "avatar_url", length = 500, nullable = false)
    private String avatarUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "experience_detail", columnDefinition = "TEXT")
    private String experienceDetail;

    @Column(name = "years_of_experience", columnDefinition = "TINYINT UNSIGNED", nullable = false)
    @Builder.Default
    private Integer yearsOfExperience = 0;

    @Column(name = "facebook_url", length = 255)
    private String facebookUrl;

    @Column(name = "instagram_url", length = 255)
    private String instagramUrl;

    @Column(name = "tiktok_url", length = 255)
    private String tiktokUrl;

    @Column(name = "is_displayed", nullable = false)
    @Builder.Default
    private Boolean isDisplayed = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaffProfile that = (StaffProfile) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
