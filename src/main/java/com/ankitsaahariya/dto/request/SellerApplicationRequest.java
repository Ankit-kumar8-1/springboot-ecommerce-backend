package com.ankitsaahariya.dto.request;

import com.ankitsaahariya.domain.BusinessType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SellerApplicationRequest {

    // BUSINESS DETAILS
    @NotBlank
    @Size(min = 3, max = 100)
    private String businessName;

    @NotNull
    private BusinessType businessType;

    @NotBlank
    @Size(max = 500)
    private String businessAddress;

    @NotBlank
    private String businessCity;

    @NotBlank
    private String businessState;

    @NotBlank
    @Pattern(regexp = "^[0-9]{6}$")
    private String businessPincode;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$")
    private String businessPhone;

    @Email
    private String businessEmail;

    @Size(max = 1000)
    private String businessDescription;

    // LEGAL
    @NotBlank
    @Pattern(
            regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$"
    )
    private String gstNumber;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$")
    private String panNumber;

    @Pattern(regexp = "^[0-9]{12}$")
    private String aadharNumber;

    // BANK
    @NotBlank
    private String bankAccountNumber;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$")
    private String bankIfscCode;

    @NotBlank
    private String bankAccountHolderName;

    @NotBlank
    private String bankName;

    private String bankBranch;
}

