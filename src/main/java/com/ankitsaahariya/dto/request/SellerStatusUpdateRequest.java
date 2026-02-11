package com.ankitsaahariya.dto.request;


import com.ankitsaahariya.domain.SellerVerificationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SellerStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private SellerVerificationStatus status;

    @Size(max = 1000, message = "Remarks must be less than 1000 characters")
    private String remarks;
}
