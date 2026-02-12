package com.ankitsaahariya.controller;

import com.ankitsaahariya.Service.AdminCategoryService;
import com.ankitsaahariya.dto.request.CategoryRequest;
import com.ankitsaahariya.dto.response.CategoryResponse;
import com.ankitsaahariya.entities.Category;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    @PostMapping("/create")
    public ResponseEntity<CategoryResponse> createCategory(@Valid  @RequestBody CategoryRequest request){
        return ResponseEntity.ok(adminCategoryService.createCategory(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable long id,
            @Valid @RequestBody CategoryRequest request){
        return ResponseEntity.ok(adminCategoryService.updateCategory(id,request));
    }

}
