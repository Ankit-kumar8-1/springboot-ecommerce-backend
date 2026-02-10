package com.ankitsaahariya.dto.request;

import com.ankitsaahariya.domain.BusinessType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SellerApplicationRequest {

    // ================= BUSINESS DETAILS =================
    @NotBlank(message = "Business name is required")
    @Size(min = 3, max = 100)
    private String businessName;

    @NotNull(message = "Business type is required")
    private BusinessType businessType;

    @NotBlank(message = "Business address is required")
    @Size(max = 500)
    private String businessAddress;

    @NotBlank(message = "City is required")
    private String businessCity;

    @NotBlank(message = "State is required")
    private String businessState;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Invalid pincode")
    private String businessPincode;

    @NotBlank(message = "Business phone is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid phone number")
    private String businessPhone;

    @Email(message = "Invalid email")
    private String businessEmail;

    @Size(max = 1000)
    private String businessDescription;

    // ================= LEGAL DETAILS =================
    @NotBlank(message = "GST number is required")
    @Pattern(
            regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
            message = "Invalid GST number format"
    )
    private String gstNumber;

    @NotBlank(message = "PAN number is required")
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN format")
    private String panNumber;

    @Pattern(regexp = "^[0-9]{12}$", message = "Invalid Aadhar number")
    private String aadharNumber;


    @NotBlank(message = "Bank account number is required")
    private String bankAccountNumber;

    @NotBlank(message = "IFSC code is required")
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code")
    private String bankIfscCode;

    @NotBlank(message = "Account holder name is required")
    private String bankAccountHolderName;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    private String bankBranch;
}
