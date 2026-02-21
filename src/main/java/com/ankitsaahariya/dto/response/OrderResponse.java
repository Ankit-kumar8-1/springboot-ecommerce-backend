package com.ankitsaahariya.dto.response;


import com.ankitsaahariya.domain.OrderStatus;
import com.ankitsaahariya.domain.PaymentMethod;
import com.ankitsaahariya.domain.PaymentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderResponse {

    private Long id;
    private String orderId;  // Unique order identifier

    // User Info
    private Long userId;
    private String userEmail;
    private String userName;

    // Items
    private List<OrderItemResponse> orderItems = new ArrayList<>();
    private Integer totalItem;

    // Pricing
    private Double totalMrpPrice;
    private Integer totalSellingPrice;
    private Integer discount;

    // Delivery Address
    private AddressResponse shippingAddress;

    // Payment Details
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String paymentId;

    // Order Status & Dates
    private OrderStatus orderStatus;
    private LocalDateTime orderDate;
    private LocalDateTime deliverDate;

    // Seller Info (for seller/admin view)
    private Long sellerId;
    private String sellerBusinessName;
}
