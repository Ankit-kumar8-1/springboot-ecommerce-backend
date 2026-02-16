package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long>, JpaSpecificationExecutor<Product> {

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

    @Query("""
        SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END
        FROM OrderItem oi
        WHERE oi.product.id = :productId
    """)
    boolean existsInOrders(@Param("productId") Long productId);

    Page<Product> findBySellerId(Long sellerId, Pageable pageable);
}
