package com.ankitsaahariya.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductRequest {

    @NotBlank(message = "Product title is required")
    @Size(min = 10, max = 200, message = "Title must be between 10 and 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 20, max = 2000, message = "Description must be between 20 and 2000 characters")
    private String description;

    @NotNull(message = "MRP price is required")
    @Min(value = 1, message = "MRP price must be at least 1")
    private Integer mrpPrice;

    @NotNull(message = "Selling price is required")
    @Min(value = 1, message = "Selling price must be at least 1")
    private Integer sellingPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @Size(max = 50, message = "Color name cannot exceed 50 characters")
    private String color;

    @NotEmpty(message = "At least one image is required")
    @Size(min = 1, max = 10, message = "You can upload between 1 and 10 images")
    private List<String> images = new ArrayList<>();

    @NotNull(message = "Category is required")
    private Long categoryId;

    @Size(max = 100, message = "Sizes cannot exceed 100 characters")
    private String sizes; // "S,M,L,XL" or "6,7,8,9,10"
}
