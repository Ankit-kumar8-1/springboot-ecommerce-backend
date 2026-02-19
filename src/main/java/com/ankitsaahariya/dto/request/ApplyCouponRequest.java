package com.ankitsaahariya.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplyCouponRequest {

    @NotBlank(message = "coupon  code is required ")
    private String couponCode;
}
