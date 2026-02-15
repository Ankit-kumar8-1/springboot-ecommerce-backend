package com.ankitsaahariya.Service;

import com.ankitsaahariya.dto.response.CategoryResponse;

import java.util.List;

public interface PublicCategoryService {

    List<CategoryResponse> getRootCategories();

    CategoryResponse getCategoryTreeById(Long id);

    CategoryResponse getCategoryBySlug(String slug);

    List<CategoryResponse> getSubCategories(Long parentId);



}
