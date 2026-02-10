package com.ankitsaahariya.dto.response;

import com.ankitsaahariya.domain.BusinessType;
import com.ankitsaahariya.domain.SellerVerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerProfileResponse {


    private Long id;
    private Long userId;
    private String userEmail;


    private SellerVerificationStatus verificationStatus;
    private Boolean isActive;
    private LocalDateTime appliedAt;


    private String businessName;
    private BusinessType businessType;
    private String businessAddress;
    private String businessCity;
    private String businessState;
    private String businessPincode;
    private String businessPhone;
    private String businessEmail;
    private String businessDescription;


    private String gstNumber;
    private String panNumber;

}
