package com.ankitsaahariya.dto.response;

import com.ankitsaahariya.domain.SellerVerificationStatus;
import com.ankitsaahariya.entities.SellerProfile;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SellerProfileResponse {

    private Long id;
    private String businessName;
    private String gstNumber;
    private SellerVerificationStatus verificationStatus;
    private LocalDateTime appliedAt;

    public static SellerProfileResponse fromEntity(SellerProfile profile) {

        SellerProfileResponse response = new SellerProfileResponse();
        response.setId(profile.getId());
        response.setBusinessName(profile.getBusinessName());
        response.setGstNumber(profile.getGstNumber());
        response.setVerificationStatus(profile.getVerificationStatus());
        response.setAppliedAt(profile.getAppliedAt());

        return response;
    }
}
