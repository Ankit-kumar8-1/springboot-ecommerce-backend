package com.ankitsaahariya.Service;

import com.ankitsaahariya.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    // Get all active root categories
    List<CategoryResponse> getRootCategories();

    // Get category by slug
    CategoryResponse getCategoryBySlug(String slug);

    // Get subcategories of a category
    List<CategoryResponse> getSubCategories(Long parentId);
}
