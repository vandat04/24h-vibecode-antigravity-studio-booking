package com.studio.config;

import com.studio.entity.ServicePackage;
import com.studio.entity.ServiceType;
import com.studio.repository.ServicePackageRepository;
import com.studio.repository.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final ServiceTypeRepository serviceTypeRepository;
    private final ServicePackageRepository servicePackageRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking database seeding status for ServiceType...");
        
        // 1. Seed ServiceTypes if empty
        if (serviceTypeRepository.count() == 0) {
            log.info("Seeding default service types (STANDARD, PREMIUM, VIP)...");
            List<ServiceType> defaultTypes = Arrays.asList(
                ServiceType.builder().serviceName("STANDARD").build(),
                ServiceType.builder().serviceName("PREMIUM").build(),
                ServiceType.builder().serviceName("VIP").build()
            );
            serviceTypeRepository.saveAll(defaultTypes);
            log.info("Seeding default service types completed!");
        }

        // 2. Associate existing packages with seeded service types
        List<ServicePackage> packages = servicePackageRepository.findAll();
        boolean updatedAny = false;
        
        for (ServicePackage pkg : packages) {
            if (pkg.getServiceType() == null) {
                String slug = pkg.getSlug() != null ? pkg.getSlug().toLowerCase() : "";
                String name = pkg.getPackageName() != null ? pkg.getPackageName().toLowerCase() : "";
                
                ServiceType typeToAssign = null;
                
                if (slug.contains("basic") || slug.contains("standard") || name.contains("basic") || name.contains("standard") || (pkg.getPrice() != null && pkg.getPrice().doubleValue() <= 1000000)) {
                    typeToAssign = serviceTypeRepository.findByServiceName("STANDARD").orElse(null);
                } else if (slug.contains("premium") || slug.contains("couple") || name.contains("premium") || name.contains("couple") || (pkg.getPrice() != null && pkg.getPrice().doubleValue() <= 2500000)) {
                    typeToAssign = serviceTypeRepository.findByServiceName("PREMIUM").orElse(null);
                } else {
                    typeToAssign = serviceTypeRepository.findByServiceName("VIP").orElse(null);
                }
                
                if (typeToAssign != null) {
                    pkg.setServiceType(typeToAssign);
                    servicePackageRepository.save(pkg);
                    log.info("Associated package '{}' (slug: {}) with service type '{}'", 
                        pkg.getPackageName(), pkg.getSlug(), typeToAssign.getServiceName());
                    updatedAny = true;
                }
            }
        }
        
        if (updatedAny) {
            log.info("Completed packages classification migration!");
        } else {
            log.info("All packages are already correctly classified.");
        }
    }
}
