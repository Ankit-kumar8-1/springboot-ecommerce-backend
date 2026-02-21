package com.ankitsaahariya.domain;

public enum OrderStatus {
    PENDING,           // Order placed, awaiting confirmation
    CONFIRMED,         // Seller confirmed
    PACKED,            // Seller packed the items
    SHIPPED,           // Order shipped
    OUT_FOR_DELIVERY,  // Out for delivery
    DELIVERED,         // Successfully delivered
    CANCELLED          // Cancelled by user/seller/admin
}
