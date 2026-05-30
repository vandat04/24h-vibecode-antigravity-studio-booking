package com.studio.repository;

import com.studio.entity.ConceptImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConceptImageRepository extends JpaRepository<ConceptImage, Long> {
    List<ConceptImage> findByConceptIdOrderBySortOrderAsc(Long conceptId);
}
