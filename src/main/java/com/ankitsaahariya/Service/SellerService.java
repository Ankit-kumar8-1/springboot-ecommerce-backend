package com.ankitsaahariya.Service;

import com.ankitsaahariya.dto.request.SellerApplicationRequest;
import com.ankitsaahariya.dto.response.MessageResponse;

public interface SellerService {

    MessageResponse requestSellerIntent(String email);

    MessageResponse verifySellerIntent(String token);
}
