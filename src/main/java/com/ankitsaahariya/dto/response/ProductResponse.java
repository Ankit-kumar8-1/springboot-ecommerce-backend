package com.ankitsaahariya.dto.response;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ProductResponse {

    private Long id;
    private String title;
    private String description;

    private Integer mrpPrice;
    private Integer sellingPrice;
    private Integer discountPercent;

    private Integer quantity;
    private String color;
    private List<String> images = new ArrayList<>();
    private String sizes;

    // Category Info
    private Long categoryId;
    private String categoryName;
    private String categorySlug;

    // Seller Info
    private Long sellerId;
    private String sellerBusinessName;
    private Double sellerRating;

    // Ratings & Reviews
    private Integer numRatings;
    private List<ReviewSummary> recentReviews = new ArrayList<>();

    private LocalDateTime createdAt;

    // Helper class for review summary
    @Data
    @NoArgsConstructor
    public static class ReviewSummary {
        private Long id;
        private String reviewText;
        private String rating;
        private String userName;
        private LocalDateTime createdAt;
    }
}

