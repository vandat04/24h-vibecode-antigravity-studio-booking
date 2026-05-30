package com.studio.service;

import com.studio.entity.Concept;
import java.util.List;

public interface ConceptService {
    Concept createConcept(Concept concept, List<String> imageUrls);
    Concept updateConcept(Long id, Concept concept, List<String> imageUrls);
    Concept getConceptBySlug(String slug);
    List<Concept> getPublishedConcepts();
    void deleteConcept(Long id);
}
