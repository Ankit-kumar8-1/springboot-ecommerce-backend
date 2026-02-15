package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    // Find by slug
    Optional<Category> findBySlug(String slug);

    // Check existence
    boolean existsByName(String name);
    boolean existsBySlug(String slug);

    // Check if category has children
    boolean existsByParentCategoryId(Long parentId);

    // ROOT + SUBCATEGORIES (single query)
    @Query("""
        SELECT DISTINCT c
        FROM Category c
        LEFT JOIN FETCH c.subCategories sc
        WHERE c.parentCategory IS NULL
        AND c.active = true
        ORDER BY c.displayOrder ASC
    """)
    List<Category> findActiveRootCategoriesWithChildren();


}
