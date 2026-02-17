package com.ankitsaahariya.controller;

import com.ankitsaahariya.Service.PublicProductService;
import com.ankitsaahariya.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/products")
public class PublicProductController {

    private final PublicProductService publicProductService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String sizes,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                publicProductService.getAllProducts(
                        pageable,
                        categoryId,
                        sellerId,
                        minPrice,
                        maxPrice,
                        color,
                        sizes,
                        minRating,
                        search,
                        sort
                )
        );
    }

    @GetMapping("/getById/{productId}")
    public  ResponseEntity<ProductResponse> getProductById(@PathVariable Long productId){
        return ResponseEntity.ok(publicProductService.getProductById(productId));
    }

    @GetMapping("/getByCategory/{categoryId}")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
                publicProductService.getProductsByCategory(categoryId, pageable));
    }

    @GetMapping("/getProductBySeller/{sellerID}")
    public ResponseEntity<Page<ProductResponse>> getProductBySeller(
            @PathVariable Long sellerID,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page,size);
        return ResponseEntity.ok(publicProductService.getProductsBySeller(sellerID,pageable));
    }
}
