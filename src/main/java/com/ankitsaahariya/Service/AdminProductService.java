package com.ankitsaahariya.Service;

import com.ankitsaahariya.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminProductService {

    Page<ProductResponse> getAllProducts(
            Pageable pageable,
            Long categoryId,
            Long sellerId,
            String search
    );
}
