package com.ankitsaahariya.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordUsingOtpRequest {

    @Size(min = 6, max = 6, message = "OTP must be exactly 6 digits")
    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "\\d{6}", message = "OTP must contain only digits")
    private String otp;

    @NotBlank(message = "new Password is must required")
    @Size(min = 6,message = "Password size At least 6 character")
    private String newPassword;

    @NotBlank(message = "Email is required")
    @Email
    private String email;
}
