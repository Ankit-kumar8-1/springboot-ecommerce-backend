package com.ankitsaahariya.dto.response;

import com.ankitsaahariya.domain.PaymentOrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PaymentResponse {

    private Long paymentOrderId;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private Long amount;
    private PaymentOrderStatus status;

    // Created orders (multi-seller)
    private List<OrderResponse> orders = new ArrayList<>();

    private String message;
}
