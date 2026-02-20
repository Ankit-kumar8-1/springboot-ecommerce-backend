package com.ankitsaahariya.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {

    @NotBlank(message = "Email is required ")
    @Email(message = "Invalid Email formate")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6,message = "Password must be at least 6 characters")
    private  String password;

    @NotBlank(message = "Name is required")
    private String fullName;

    @NotBlank
    @Pattern(regexp = "^[6-9][0-9]{9}$")
    private String mobileNumber;
}
