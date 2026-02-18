package com.ankitsaahariya.dto.response;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CartResponse {
    private Long id;

    // Items
    private List<CartItemResponse> items = new ArrayList<>();
    private Integer totalItems;  // Total quantity of all items

    // Pricing
    private Integer totalMrpPrice;
    private Integer totalSellingPrice;
    private Integer discount;  // totalMrpPrice - totalSellingPrice

    // Coupon
    private String couponCode;
    private Integer couponDiscount;  // Additional discount from coupon
    private Double couponPercentage;

    // Final
    private Integer finalPrice;  // totalSellingPrice - couponDiscount

    // Savings
    private Integer totalSavings;
}
