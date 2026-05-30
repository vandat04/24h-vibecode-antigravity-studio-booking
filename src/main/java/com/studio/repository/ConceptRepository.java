package com.studio.repository;

import com.studio.constant.PublishStatus;
import com.studio.entity.Concept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConceptRepository extends JpaRepository<Concept, Long> {
    Optional<Concept> findBySlug(String slug);
    List<Concept> findByStatus(PublishStatus status);
    Optional<Concept> findBySlugAndStatus(String slug, PublishStatus status);
}
