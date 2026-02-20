package com.ankitsaahariya.dto.request;

import com.ankitsaahariya.domain.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2,max = 100 ,message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Mobil Number is required ")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Invalid mobil number")
    private String mobile;

    @NotBlank(message = "Address is required")
    @Size(min = 10,max = 200,message = "Address must be between 10 to 100 characters")
    private String address;

    @NotBlank(message = "Locality is required")
    @Size(max = 100,message = "Locality can not be exceed 100 characters")
    private String locality;

    @NotBlank(message = "City is required ")
    @Size(max = 50,message = "city name cannot be exceed 50 characters")
    private String city;

    @NotBlank(message = "state is required")
    @Size(max = 50,message = "state name cannot exceed 50 characters")
    private String state;

    @NotBlank(message = "PinCode is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$",message = "Invalid Pin code")
    private String pinCode;

    @NotNull(message = "Address type is required")
    private AddressType addressType;
}
