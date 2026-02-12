package com.ankitsaahariya.Service;

import com.ankitsaahariya.dto.request.CategoryRequest;
import com.ankitsaahariya.dto.response.CategoryResponse;

import java.util.List;

public interface AdminCategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

}
