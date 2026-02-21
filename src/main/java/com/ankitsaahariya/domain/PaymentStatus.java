package com.ankitsaahariya.domain;

public enum PaymentStatus {
    PENDING,          // Payment not yet completed
    PROCESSING,       // Payment in process
    COMPLETED,        // Payment successful
    FAILED,           // Payment failed
    REFUNDED          // Payment refunded
}
