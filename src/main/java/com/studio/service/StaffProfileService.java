package com.studio.service;

import com.studio.entity.StaffProfile;
import java.util.List;

public interface StaffProfileService {
    StaffProfile getProfileByUserId(Long userId);
    StaffProfile saveOrUpdateProfile(Long userId, StaffProfile profile);
    List<StaffProfile> getDisplayedProfiles();
}
