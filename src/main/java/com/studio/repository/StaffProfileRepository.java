package com.studio.repository;

import com.studio.entity.StaffProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffProfileRepository extends JpaRepository<StaffProfile, Long> {
    Optional<StaffProfile> findByUserId(Long userId);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM StaffProfile p JOIN FETCH p.user u JOIN FETCH u.role")
    java.util.List<StaffProfile> findAllWithUserAndRole();
}
