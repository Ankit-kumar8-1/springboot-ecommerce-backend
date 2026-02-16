package com.ankitsaahariya.controller;

import com.ankitsaahariya.Service.SellerProductService;
import com.ankitsaahariya.dto.request.ProductRequest;
import com.ankitsaahariya.dto.response.MessageResponse;
import com.ankitsaahariya.dto.response.ProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class SellerProductController {
    private final SellerProductService sellerProductService;

    @PostMapping("/create")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request){
        return ResponseEntity.ok(sellerProductService.createProduct(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequest request
    ){
        return ResponseEntity.ok(sellerProductService.updateProduct(id,request));
    }

    @DeleteMapping("delete/{productId}")
    public ResponseEntity<MessageResponse> deleteProduct(@PathVariable Long productId){
        return ResponseEntity.ok(sellerProductService.deleteProduct(productId));
    }

}