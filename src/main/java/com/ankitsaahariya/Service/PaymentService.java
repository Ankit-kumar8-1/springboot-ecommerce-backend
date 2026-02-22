package com.ankitsaahariya.Service;

import com.ankitsaahariya.dto.request.PaymentVerificationRequest;
import com.ankitsaahariya.dto.response.PaymentResponse;
import org.json.JSONObject;

public interface PaymentService {

    JSONObject createPaymentOrder(Long addressId) throws Exception;

    PaymentResponse verifyPayment(PaymentVerificationRequest request) throws Exception;
}
