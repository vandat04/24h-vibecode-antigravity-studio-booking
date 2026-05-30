package com.studio.service.impl;

import com.studio.entity.StaffProfile;
import com.studio.entity.User;
import com.studio.repository.StaffProfileRepository;
import com.studio.repository.UserRepository;
import com.studio.service.StaffProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StaffProfileServiceImpl implements StaffProfileService {

    private final StaffProfileRepository staffProfileRepository;
    private final UserRepository userRepository;

    @Override
    public StaffProfile getProfileByUserId(Long userId) {
        return staffProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Staff profile not found for user ID: " + userId));
    }

    @Override
    @Transactional
    public StaffProfile saveOrUpdateProfile(Long userId, StaffProfile updatedProfile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        return staffProfileRepository.findByUserId(userId)
                .map(existingProfile -> {
                    existingProfile.setAvatarUrl(updatedProfile.getAvatarUrl());
                    existingProfile.setBio(updatedProfile.getBio());
                    existingProfile.setExperienceDetail(updatedProfile.getExperienceDetail());
                    existingProfile.setYearsOfExperience(updatedProfile.getYearsOfExperience());
                    existingProfile.setFacebookUrl(updatedProfile.getFacebookUrl());
                    existingProfile.setInstagramUrl(updatedProfile.getInstagramUrl());
                    existingProfile.setTiktokUrl(updatedProfile.getTiktokUrl());
                    existingProfile.setIsDisplayed(updatedProfile.getIsDisplayed());
                    return staffProfileRepository.save(existingProfile);
                })
                .orElseGet(() -> {
                    updatedProfile.setUser(user);
                    return staffProfileRepository.save(updatedProfile);
                });
    }

    @Override
    public List<StaffProfile> getDisplayedProfiles() {
        return staffProfileRepository.findAll().stream()
                .filter(StaffProfile::getIsDisplayed)
                .collect(Collectors.toList());
    }
}
