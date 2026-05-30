package com.studio.repository;

import com.studio.entity.StudioInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudioInformationRepository extends JpaRepository<StudioInformation, Integer> {
}
