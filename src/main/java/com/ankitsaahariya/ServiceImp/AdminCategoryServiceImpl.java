package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Service.AdminCategoryService;
import com.ankitsaahariya.dao.CategoryRepository;
import com.ankitsaahariya.dao.ProductRepository;
import com.ankitsaahariya.dto.request.CategoryRequest;
import com.ankitsaahariya.dto.response.CategoryResponse;
import com.ankitsaahariya.dto.response.MessageResponse;
import com.ankitsaahariya.entities.Category;
import com.ankitsaahariya.entities.Product;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.util.List;

@Service
@AllArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

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

        if(!category.getActive()){
            throw new RuntimeException("We not delete active category !");
        }
        // Check child categories
        if (categoryRepository.existsByParentCategoryId(categoryId)) {
            throw new RuntimeException("Cannot delete category with subcategories");
        }
        // Check products
        if (productRepository.existsByCategoryId(categoryId)) {
            throw new RuntimeException("Cannot delete category with assigned products");
        }

        categoryRepository.delete(category);
        return new MessageResponse("Category Delete successfully !");
    }

    @Override
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return categoryPage.map(this::mapToResponse);
    }


    @Override
    public List<CategoryResponse> searchCategories(String keyword) {

        List<Category> categories =
                categoryRepository.findByNameContainingIgnoreCaseAndActiveTrue(keyword);

        return categories.stream()
                .map(category -> {
                    CategoryResponse res = new CategoryResponse();

                    res.setId(category.getId());
                    res.setName(category.getName());
                    res.setSlug(category.getSlug());
                    res.setImageUrl(category.getImageUrl());

                    return res;
                })
                .toList();
    }

    //    Mapper function
    private CategoryResponse mapToResponse(Category category) {

        CategoryResponse response = new CategoryResponse();

        response.setId(category.getId());
        response.setName(category.getName());
        response.setSlug(category.getSlug());
        response.setDescription(category.getDescription());
        response.setImageUrl(category.getImageUrl());
        response.setActive(category.getActive());
        response.setDisplayOrder(category.getDisplayOrder());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());

        // ⭐ PARENT INFO ADD KARO
        if (category.getParentCategory() != null) {
            response.setParentCategoryId(category.getParentCategory().getId());
            response.setParentCategoryName(category.getParentCategory().getName());
        }

        // ⭐ PRODUCT COUNT ADD KARO
        Integer count = productRepository.countByCategoryId(category.getId());
        response.setProductCount(count);

        return response;
    }
}
