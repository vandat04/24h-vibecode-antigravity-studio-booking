package com.studio.repository;

import com.studio.entity.CustomerStory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerStoryRepository extends JpaRepository<CustomerStory, Long> {
    List<CustomerStory> findByIsDisplayedTrueOrderByCreatedAtDesc();
}
