package com.ankitsaahariya.dto.response;

import com.ankitsaahariya.domain.AddressType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    private Long id;
    private String name;
    private String mobile;
    private String address;
    private String locality;
    private String city;
    private String state;
    private String pinCode;
    private AddressType addressType;
    private Boolean isDefault;

    // User info (optional - for admin view)
    private Long userId;
    private String userEmail;
}
