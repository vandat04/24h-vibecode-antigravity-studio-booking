package com.studio.service.impl;

import com.studio.constant.PublishStatus;
import com.studio.entity.Blog;
import com.studio.entity.Concept;
import com.studio.repository.BlogRepository;
import com.studio.repository.ConceptRepository;
import com.studio.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final ConceptRepository conceptRepository;

    @Override
    @Transactional
    public Blog createBlog(Blog blog, Long conceptId) {
        if (conceptId != null) {
            Concept concept = conceptRepository.findById(conceptId)
                    .orElseThrow(() -> new IllegalArgumentException("Concept not found with ID: " + conceptId));
            blog.setConcept(concept);
        }
        return blogRepository.save(blog);
    }

    @Override
    @Transactional
    public Blog updateBlog(Long id, Blog updatedBlog, Long conceptId) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Blog post not found with ID: " + id));

        blog.setTitle(updatedBlog.getTitle());
        blog.setSlug(updatedBlog.getSlug());
        blog.setThumbnailUrl(updatedBlog.getThumbnailUrl());
        blog.setContent(updatedBlog.getContent());
        blog.setStatus(updatedBlog.getStatus());

        if (conceptId != null) {
            Concept concept = conceptRepository.findById(conceptId)
                    .orElseThrow(() -> new IllegalArgumentException("Concept not found with ID: " + conceptId));
            blog.setConcept(concept);
        } else {
            blog.setConcept(null);
        }

        return blogRepository.save(blog);
    }

    @Override
    public Blog getBlogBySlug(String slug) {
        return blogRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Blog post not found with slug: " + slug));
    }

    @Override
    public List<Blog> getPublishedBlogs() {
        return blogRepository.findByStatusOrderByCreatedAtDesc(PublishStatus.PUBLISHED);
    }

    @Override
    @Transactional
    public void deleteBlog(Long id) {
        if (!blogRepository.existsById(id)) {
            throw new IllegalArgumentException("Blog post not found with ID: " + id);
        }
        blogRepository.deleteById(id);
    }
}
