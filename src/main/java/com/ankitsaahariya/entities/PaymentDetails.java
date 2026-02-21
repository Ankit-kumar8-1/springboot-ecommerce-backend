package com.ankitsaahariya.entities;

import com.ankitsaahariya.domain.PaymentMethod;
import com.ankitsaahariya.domain.PaymentStatus;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetails {

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private String paymentId;           // Payment gateway transaction ID

    private String razorpayOrderId;     // For Razorpay integration

    private String razorpayPaymentId;   // For Razorpay integration

    private LocalDateTime paymentDate;
}
