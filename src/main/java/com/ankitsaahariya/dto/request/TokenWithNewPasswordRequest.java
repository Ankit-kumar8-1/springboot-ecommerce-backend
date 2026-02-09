package com.ankitsaahariya.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TokenWithNewPasswordRequest {

    @NotBlank(message = "token is required")
    private String token;

    @NotBlank(message = "New password must Required")
    @Size(min = 6,message = "Password Length must be greater than 6")
    private String newPassword;
}
