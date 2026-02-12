package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    // Find by slug
    Optional<Category> findBySlug(String slug);

    // Check existence
    boolean existsByName(String name);
    boolean existsBySlug(String slug);

    // Root categories (active only)
    List<Category> findByParentCategoryIsNullAndActiveTrueOrderByDisplayOrderAsc();

    // All root categories (admin use)
    List<Category> findByParentCategoryIsNull();

    // Active subcategories of a parent
    List<Category> findByParentCategoryIdAndActiveTrueOrderByDisplayOrderAsc(Long parentId);

    // All active categories
    List<Category> findByActiveTrueOrderByDisplayOrderAsc();
}
