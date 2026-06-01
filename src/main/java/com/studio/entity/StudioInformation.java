package com.studio.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "studio_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudioInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "TINYINT UNSIGNED")
    private Integer id;

    @Column(name = "studio_name", length = 150, nullable = false)
    private String studioName;

    @Column(name = "logo_url", length = 500, nullable = false)
    private String logoUrl;

    @Column(name = "banner_url", length = 500, nullable = false)
    private String bannerUrl;

    @Column(length = 255, nullable = false)
    private String address;

    @Column(length = 15, nullable = false)
    private String phone;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(name = "facebook_url", length = 255)
    private String facebookUrl;

    @Column(name = "zalo_url", length = 255)
    private String zaloUrl;

    @Column(name = "youtube_url", length = 255)
    private String youtubeUrl;

    @Column(name = "instagram_url", length = 255)
    private String instagramUrl;

    @Column(name = "tiktok_url", length = 255)
    private String tiktokUrl;

    @Column(name = "intro_video_url", length = 255)
    private String introVideoUrl;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String introduction;

    @Column(name = "working_process", columnDefinition = "TEXT", nullable = false)
    private String workingProcess;

    @Column(name = "google_map_url", columnDefinition = "TEXT", nullable = false)
    private String googleMapUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudioInformation that = (StudioInformation) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
