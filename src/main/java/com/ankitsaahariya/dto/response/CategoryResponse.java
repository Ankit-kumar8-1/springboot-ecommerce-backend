package com.ankitsaahariya.dto.response;

import com.ankitsaahariya.entities.Category;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String imageUrl;

    private Boolean active;
    private Integer displayOrder;


    private Long parentCategoryId;
    private String parentCategoryName;

    private Integer productCount; // optional, service layer me set hoga

    private List<CategoryResponse> subCategories;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
