package com.ankitsaahariya.dto.response;

import com.ankitsaahariya.domain.BusinessType;
import com.ankitsaahariya.domain.SellerVerificationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SellerApplicationDetailResponse {

    private Long sellerProfileId;

    // USER INFO
    private Long userId;
    private String fullName;
    private String email;

    // BUSINESS INFO
    private String businessName;
    private BusinessType businessType;
    private String businessAddress;
    private String businessCity;
    private String businessState;
    private String businessPincode;
    private String businessPhone;
    private String businessEmail;
    private String businessDescription;

    // LEGAL INFO
    private String gstNumber;
    private String panNumber;
    private String aadharNumber;

    // BANK INFO
    private String bankAccountNumber;
    private String bankIfscCode;
    private String bankAccountHolderName;
    private String bankName;
    private String bankBranch;

    // STATUS INFO
    private SellerVerificationStatus verificationStatus;
    private LocalDateTime appliedAt;
    private LocalDateTime verifiedAt;
    private String adminRemarks;
}
