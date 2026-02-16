package com.ankitsaahariya.Service;


import com.ankitsaahariya.dto.request.ProductRequest;
import com.ankitsaahariya.dto.response.ProductResponse;

public interface SellerProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Long productId, ProductRequest request);
}
