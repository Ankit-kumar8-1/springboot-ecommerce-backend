package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {

    boolean existsByCategoryId(Long categoryId);

    Integer countByCategoryId(Long categoryId);

    // Batch count for multiple categories (avoid N+1)
    @Query("""
        SELECT p.category.id, COUNT(p.id)
        FROM Product p
        WHERE p.category.id IN :categoryIds
        GROUP BY p.category.id
    """)
    List<Object[]> countProductsByCategoryIds(List<Long> categoryIds);
}
