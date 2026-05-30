package com.studio.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudioInfoResponse {
    private Integer id;
    private String studioName;
    private String logoUrl;
    private String bannerUrl;
    private String address;
    private String phone;
    private String email;
    private String facebookUrl;
    private String zaloUrl;
    private String youtubeUrl;
    private String introVideoUrl;
    private String introduction;
    private String workingProcess;
    private String googleMapUrl;
}
