package com.ankitsaahariya.controller;

import com.ankitsaahariya.Service.PublicCategoryService;
import com.ankitsaahariya.dto.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
