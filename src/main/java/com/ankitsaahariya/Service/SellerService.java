package com.ankitsaahariya.Service;

import com.ankitsaahariya.domain.SellerVerificationStatus;
import com.ankitsaahariya.dto.request.SellerApplicationRequest;
import com.ankitsaahariya.dto.response.MessageResponse;
import com.ankitsaahariya.dto.response.PageResponse;
import com.ankitsaahariya.dto.response.SellerProfileResponse;

public interface SellerService {

    MessageResponse requestSellerIntent(String email);

    MessageResponse verifySellerIntent(String token);

    MessageResponse applyForSeller(SellerApplicationRequest request);

    PageResponse<SellerProfileResponse> getSellerApplications(
            SellerVerificationStatus status,
            int page,
            int size
    );
}
