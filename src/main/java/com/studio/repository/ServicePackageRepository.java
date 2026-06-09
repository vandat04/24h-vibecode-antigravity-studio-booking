package com.studio.repository;

import com.studio.entity.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicePackageRepository extends JpaRepository<ServicePackage, Long> {
    Optional<ServicePackage> findBySlug(String slug);
    List<ServicePackage> findByIsActiveTrue();
    List<ServicePackage> findByIsActiveTrueAndServiceType_Id(Long serviceTypeId);
    Optional<ServicePackage> findBySlugAndIsActiveTrue(String slug);
}
