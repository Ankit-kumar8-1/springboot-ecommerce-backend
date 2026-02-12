package com.ankitsaahariya.Service;

import com.ankitsaahariya.dto.request.CategoryRequest;
import com.ankitsaahariya.dto.response.CategoryResponse;
import com.ankitsaahariya.dto.response.MessageResponse;

import java.util.List;

public interface AdminCategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    MessageResponse deleteCategory(Long categoryId);

}
