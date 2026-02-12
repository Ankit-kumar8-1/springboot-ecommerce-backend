package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Service.AdminCategoryService;
import com.ankitsaahariya.dao.CategoryRepository;
import com.ankitsaahariya.dto.request.CategoryRequest;
import com.ankitsaahariya.dto.response.CategoryResponse;
import com.ankitsaahariya.entities.Category;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {

        if(categoryRepository.existsByName(request.getName())){
            throw new RuntimeException("Category with this name is already exists !");
        }

        Category category = Category.builder()
                .name(request.getName())
                .slug(request.getSlug()) // null allowed, auto generate in entity
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .displayOrder(request.getDisplayOrder())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        if(request.getParentCategoryId() != null){
            Category parent = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(()-> new RuntimeException("Parent category not found "));

            category.setParentCategory(parent);
        }

        Category saveCategory = categoryRepository.save(category);

        return mapToResponse(saveCategory);
    }

//    Mapper function
    private CategoryResponse mapToResponse(Category category) {

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .active(category.getActive())
                .displayOrder(category.getDisplayOrder())
                .parentCategoryId(
                        category.getParentCategory() != null
                                ? category.getParentCategory().getId()
                                : null
                )
                .parentCategoryName(
                        category.getParentCategory() != null
                                ? category.getParentCategory().getName()
                                : null
                )
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
