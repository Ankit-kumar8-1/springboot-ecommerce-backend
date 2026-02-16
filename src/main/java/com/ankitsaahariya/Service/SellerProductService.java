package com.ankitsaahariya.Service;


import com.ankitsaahariya.dto.request.ProductRequest;
import com.ankitsaahariya.dto.response.MessageResponse;
import com.ankitsaahariya.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SellerProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Long productId, ProductRequest request);

    MessageResponse deleteProduct(Long productId);

    Page<ProductResponse> getSellerProducts(Pageable pageable, String search);
}
