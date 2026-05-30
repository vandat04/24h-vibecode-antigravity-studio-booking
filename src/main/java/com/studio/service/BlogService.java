package com.studio.service;

import com.studio.entity.Blog;
import java.util.List;

public interface BlogService {
    Blog createBlog(Blog blog, Long conceptId);
    Blog updateBlog(Long id, Blog blog, Long conceptId);
    Blog getBlogBySlug(String slug);
    List<Blog> getPublishedBlogs();
    void deleteBlog(Long id);
}
