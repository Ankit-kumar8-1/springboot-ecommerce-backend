package com.ankitsaahariya.Service;

import org.json.JSONObject;

public interface PaymentService {

    JSONObject createPaymentOrder(Long addressId) throws Exception;
}
