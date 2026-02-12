package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Service.AdminCategoryService;
import com.ankitsaahariya.dao.CategoryRepository;
import com.ankitsaahariya.dto.request.CategoryRequest;
import com.ankitsaahariya.dto.response.CategoryResponse;
import com.ankitsaahariya.dto.response.MessageResponse;
import com.ankitsaahariya.entities.Category;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

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

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Category not found with this Id : "+ id));

        if(!category.getName().equals(request.getName())
                && categoryRepository.existsByName(request.getName())){
            throw new RuntimeException("Category with this name is already Exists !");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setDisplayOrder(request.getDisplayOrder());
        category.setSlug(request.getSlug());
        category.setImageUrl(request.getImageUrl());

        if(request.getActive() != null){
            category.setActive(request.getActive());
        }

        if(request.getSlug()!=null && !request.getSlug().isBlank()){
            category.setSlug(request.getSlug());
        }

        if (request.getParentCategoryId() != null){

            if (request.getParentCategoryId().equals(id)){
                throw new RuntimeException("Category cannot be its own parent");
            }

            Category parent = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(()-> new RuntimeException("Parent category not found"));

            category.setParentCategory(parent);
        }else {
            category.setParentCategory(null);
        }

        Category update = categoryRepository.save(category);

        return mapToResponse(update);
    }

    @Override
    public MessageResponse deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new RuntimeException("Category not found"));

        if(category.getActive()){
            throw new RuntimeException("We not delete active category !");
        }
        // Check child categories
        if (categoryRepository.existsByParentCategoryId(categoryId)) {
            throw new RuntimeException("Cannot delete category with subcategories");
        }
//        // Check products
//        if (productRepository.existsByCategoryId(categoryId)) {
//            throw new RuntimeException("Cannot delete category with assigned products");
//        }

        categoryRepository.delete(category);
        return new MessageResponse("Category Delete successfully !");
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
