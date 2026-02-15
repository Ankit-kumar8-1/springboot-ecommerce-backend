package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Service.PublicCategoryService;
import com.ankitsaahariya.dao.CategoryRepository;
import com.ankitsaahariya.dao.ProductRepository;
import com.ankitsaahariya.dto.response.CategoryResponse;
import com.ankitsaahariya.entities.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PublicCategoryServiceImpl implements PublicCategoryService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;


    @Override
    public List<CategoryResponse> getRootCategories() {

        List<Category> roots = categoryRepository
                .findByParentCategoryIsNullAndActiveTrueOrderByDisplayOrderAsc();

        List<Long> ids = roots.stream()
                .flatMap(root -> {
                    List<Category> all = new ArrayList<>();
                    all.add(root);
                    all.addAll(root.getSubCategories());
                    return all.stream();
                })
                .map(Category::getId)
                .toList();

        Map<Long,Integer> productCountMap = getProductCountMap(ids);

        return roots.stream()
                .map(root -> mapToResponse(root,productCountMap))
                .toList();
}

    @Override
    public CategoryResponse getCategoryTreeById(Long id) {
        Category category = categoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(()-> new RuntimeException("Category is not found !"));

        return mapToTree(category);
    }


    private CategoryResponse mapToTree(Category category) {
        CategoryResponse res = new CategoryResponse();

        res.setId(category.getId());
        res.setName(category.getName());
        res.setSlug(category.getSlug());
        res.setDescription(category.getDescription());
        res.setImageUrl(category.getImageUrl());
        res.setActive(category.getActive());
        res.setDisplayOrder(category.getDisplayOrder());
        res.setCreatedAt(category.getCreatedAt());
        res.setUpdatedAt(category.getUpdatedAt());

        if (category.getParentCategory() != null) {
            res.setParentCategoryId(category.getParentCategory().getId());
            res.setParentCategoryName(category.getParentCategory().getName());
        }

        List<Category> children = categoryRepository
                .findByParentCategoryIdAndActiveTrueOrderByDisplayOrderAsc(category.getId());

        List<CategoryResponse> childResponses =
                children.stream()
                        .map(this::mapToTree)
                        .toList();

        res.setSubCategories(childResponses);

        return  res;
    }

    private CategoryResponse mapToResponse(Category category, Map<Long, Integer> countMap) {

        CategoryResponse res = new CategoryResponse();

        res.setId(category.getId());
        res.setName(category.getName());
        res.setSlug(category.getSlug());
        res.setDescription(category.getDescription());
        res.setImageUrl(category.getImageUrl());
        res.setActive(category.getActive());
        res.setDisplayOrder(category.getDisplayOrder());
        res.setCreatedAt(category.getCreatedAt());
        res.setUpdatedAt(category.getUpdatedAt());

        // Parent mapping
        if (category.getParentCategory() != null) {
            res.setParentCategoryId(category.getParentCategory().getId());
            res.setParentCategoryName(category.getParentCategory().getName());
        }

        // Product count
        res.setProductCount(
                countMap.getOrDefault(category.getId(), 0)
        );

        // ⭐ SubCategories mapping (recursive)
        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {

            List<CategoryResponse> children =
                    category.getSubCategories().stream()
                            .filter(Category::getActive)
                            .map(sub -> mapToResponse(sub, countMap))
                            .toList();

            res.setSubCategories(children);
        }

        return res;
    }



    private Map<Long, Integer> getProductCountMap(List<Long> ids) {
        List<Object[]> results = productRepository.countProductsByCategoryIds(ids);
        Map<Long,Integer> map = new HashMap<>();

        for (Object[] row : results){
            map.put(
                    (Long) row[0],
                    ((Long)row[1]).intValue()
            );
        }

        return map;
    }


//last curli bracket 
}
