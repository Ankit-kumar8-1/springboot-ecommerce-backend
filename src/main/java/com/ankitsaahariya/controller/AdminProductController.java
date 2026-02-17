package com.ankitsaahariya.controller;

import com.ankitsaahariya.Service.AdminProductService;
import com.ankitsaahariya.dto.response.MessageResponse;
import com.ankitsaahariya.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitterReturnValueHandler;

import java.util.List;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final AdminProductService adminProductService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) String search
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(
                adminProductService.getAllProducts(pageable, categoryId, sellerId, search)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(adminProductService.getProductById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>>  searchByKeyword(@RequestParam String keyword){
        return ResponseEntity.ok(adminProductService.searchProducts(keyword));
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<MessageResponse> toggleProduct(@PathVariable Long id){
        return ResponseEntity.ok(adminProductService.toggleProductStatus(id));
    }


}
