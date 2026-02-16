package com.ankitsaahariya.controller;

import com.ankitsaahariya.Service.PublicCategoryService;
import com.ankitsaahariya.dto.response.CategoryResponse;
import com.ankitsaahariya.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoryController {

    private final PublicCategoryService publicCategoryService;

    @GetMapping("/root")
    public ResponseEntity<List<CategoryResponse>> getRootCategories(){
        return ResponseEntity.ok(publicCategoryService.getRootCategories());
    }

    @GetMapping("/subcategories/{id}")
    public ResponseEntity<CategoryResponse> getCategoryTree(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(publicCategoryService.getCategoryTreeById(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoryResponse> getCategoryBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(publicCategoryService.getCategoryBySlug(slug));
    }

    @GetMapping("/parent/{parentId}/subcategories")
    public ResponseEntity<List<CategoryResponse>> getSubCategories(
            @PathVariable Long parentId) {

        return ResponseEntity.ok(publicCategoryService.getSubCategories(parentId));
    }



}
