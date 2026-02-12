package com.ankitsaahariya.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
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

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
