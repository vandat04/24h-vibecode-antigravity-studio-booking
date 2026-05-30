package com.studio.service.impl;

import com.studio.constant.PublishStatus;
import com.studio.entity.Concept;
import com.studio.entity.ConceptImage;
import com.studio.repository.ConceptImageRepository;
import com.studio.repository.ConceptRepository;
import com.studio.service.ConceptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConceptServiceImpl implements ConceptService {

    private final ConceptRepository conceptRepository;

    @Override
    @Transactional
    public Concept createConcept(Concept concept, List<String> imageUrls) {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            List<ConceptImage> images = new ArrayList<>();
            for (int i = 0; i < imageUrls.size(); i++) {
                images.add(ConceptImage.builder()
                        .concept(concept)
                        .imageUrl(imageUrls.get(i))
                        .sortOrder(i)
                        .build());
            }
            concept.setImages(images);
        }
        return conceptRepository.save(concept);
    }

    @Override
    @Transactional
    public Concept updateConcept(Long id, Concept updatedConcept, List<String> imageUrls) {
        Concept concept = conceptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Concept not found with ID: " + id));

        concept.setTitle(updatedConcept.getTitle());
        concept.setSlug(updatedConcept.getSlug());
        concept.setConceptType(updatedConcept.getConceptType());
        concept.setThumbnailUrl(updatedConcept.getThumbnailUrl());
        concept.setDescription(updatedConcept.getDescription());
        concept.setStatus(updatedConcept.getStatus());

        // Refresh/rebuild image list
        concept.getImages().clear();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (int i = 0; i < imageUrls.size(); i++) {
                concept.getImages().add(ConceptImage.builder()
                        .concept(concept)
                        .imageUrl(imageUrls.get(i))
                        .sortOrder(i)
                        .build());
            }
        }

        return conceptRepository.save(concept);
    }

    @Override
    public Concept getConceptBySlug(String slug) {
        return conceptRepository.findBySlugAndStatus(slug, PublishStatus.PUBLISHED)
                .orElseThrow(() -> new IllegalArgumentException("Concept not found with slug: " + slug));
    }

    @Override
    public List<Concept> getPublishedConcepts() {
        return conceptRepository.findByStatus(PublishStatus.PUBLISHED);
    }

    @Override
    @Transactional
    public void deleteConcept(Long id) {
        if (!conceptRepository.existsById(id)) {
            throw new IllegalArgumentException("Concept not found with ID: " + id);
        }
        conceptRepository.deleteById(id);
    }
}
