package com.ankitsaahariya.Service;

import com.ankitsaahariya.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PublicProductService {

    Page<ProductResponse> getAllProducts(
            Pageable pageable,
            Long categoryId,
            Long sellerId,
            Integer minPrice,
            Integer maxPrice,
            String color,
            String sizes,
            Integer minRating,
            String search,
            String sort
    );

    ProductResponse getProductById(Long productId);

    Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable);

    Page<ProductResponse> getProductsBySeller(Long sellerId, Pageable pageable);
}
