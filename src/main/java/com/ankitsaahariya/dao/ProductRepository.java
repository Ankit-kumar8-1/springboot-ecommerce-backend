package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {

    boolean existsByCategoryId(Long categoryId);

    Integer countByCategoryId(Long categoryId);
}
