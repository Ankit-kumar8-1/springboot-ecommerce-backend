package com.ankitsaahariya.dto.response;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartItemResponse {
    private Long id;

    // Product Info
    private Long productId;
    private String productTitle;
    private String productImage;  // First image from product
    private String color;

    // Cart Item Details
    private String size;
    private Integer quantity;
    private Integer mrpPrice;
    private Integer sellingPrice;

    // Calculated
    private Integer totalMrpPrice;  // mrpPrice * quantity
    private Integer totalSellingPrice;  // sellingPrice * quantity
    private Integer itemDiscount;  // totalMrpPrice - totalSellingPrice
}
