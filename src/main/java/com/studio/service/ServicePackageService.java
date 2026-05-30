package com.studio.service;

import com.studio.entity.ServicePackage;
import java.util.List;

public interface ServicePackageService {
    ServicePackage createPackage(ServicePackage servicePackage);
    ServicePackage updatePackage(Long id, ServicePackage servicePackage);
    List<ServicePackage> getActivePackages();
    ServicePackage getPackageById(Long id);
    ServicePackage getPackageBySlug(String slug);
}
