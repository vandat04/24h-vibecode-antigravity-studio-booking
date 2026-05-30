package com.studio.service.impl;

import com.studio.entity.ServicePackage;
import com.studio.repository.ServicePackageRepository;
import com.studio.service.ServicePackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServicePackageServiceImpl implements ServicePackageService {

    private final ServicePackageRepository servicePackageRepository;

    @Override
    @Transactional
    public ServicePackage createPackage(ServicePackage servicePackage) {
        return servicePackageRepository.save(servicePackage);
    }

    @Override
    @Transactional
    public ServicePackage updatePackage(Long id, ServicePackage updatedPackage) {
        ServicePackage servicePackage = getPackageById(id);
        servicePackage.setPackageName(updatedPackage.getPackageName());
        servicePackage.setSlug(updatedPackage.getSlug());
        servicePackage.setPrice(updatedPackage.getPrice());
        servicePackage.setShortDescription(updatedPackage.getShortDescription());
        servicePackage.setDetailContent(updatedPackage.getDetailContent());
        servicePackage.setLayoutCount(updatedPackage.getLayoutCount());
        servicePackage.setOutfitCount(updatedPackage.getOutfitCount());
        servicePackage.setEditedPhotos(updatedPackage.getEditedPhotos());
        servicePackage.setMakeupPersonCount(updatedPackage.getMakeupPersonCount());
        servicePackage.setThumbnailUrl(updatedPackage.getThumbnailUrl());
        servicePackage.setIsActive(updatedPackage.getIsActive());
        return servicePackageRepository.save(servicePackage);
    }

    @Override
    public List<ServicePackage> getActivePackages() {
        return servicePackageRepository.findByIsActiveTrue();
    }

    @Override
    public ServicePackage getPackageById(Long id) {
        return servicePackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service package not found with ID: " + id));
    }

    @Override
    public ServicePackage getPackageBySlug(String slug) {
        return servicePackageRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new IllegalArgumentException("Active service package not found with slug: " + slug));
    }
}
