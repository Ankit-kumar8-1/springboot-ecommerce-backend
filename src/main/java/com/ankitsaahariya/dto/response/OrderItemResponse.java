package com.ankitsaahariya.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderItemResponse {

    private Long id;

    // Product Info
    private Long productId;
    private String productTitle;
    private String productImage;
    private String color;

    // Order Item Details
    private String size;
    private Integer quantity;
    private Integer mrpPrice;
    private Integer sellingPrice;

    // Calculated
    private Integer totalPrice; // sellingPrice * quantity

    // Seller Info
    private Long sellerId;
    private String sellerName;
}
