package com.ankitsaahariya.Service;

import com.ankitsaahariya.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminProductService {

    Page<ProductResponse> getAllProducts(
            Pageable pageable,
            Long categoryId,
            Long sellerId,
            String search
    );

    ProductResponse getProductById(Long productId);

    List<ProductResponse> searchProducts(String keyword);
}
