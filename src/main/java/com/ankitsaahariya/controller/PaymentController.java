package com.ankitsaahariya.controller;


import com.ankitsaahariya.Service.PaymentService;
import com.ankitsaahariya.dto.request.PaymentVerificationRequest;
import com.ankitsaahariya.dto.response.PaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
@PreAuthorize("isAuthenticated()")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createPaymentOrder(@RequestParam Long addressId) {
        try {
            JSONObject orderDetails = paymentService.createPaymentOrder(addressId);
            return ResponseEntity.ok(orderDetails.toMap());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @Valid @RequestBody PaymentVerificationRequest request
    ) {
        try {
            PaymentResponse response = paymentService.verifyPayment(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
