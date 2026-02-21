package com.ankitsaahariya.domain;

public enum PaymentOrderStatus {
    PENDING,          // Payment order created, awaiting payment
    PROCESSING,       // Payment in process
    COMPLETED,        // Payment successful
    FAILED,           // Payment failed
    REFUNDED,         // Payment refunded
    CANCELLED         // Payment cancelled
}
