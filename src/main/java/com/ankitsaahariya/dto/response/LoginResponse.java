package com.ankitsaahariya.dto.response;

import com.ankitsaahariya.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String email;
    private String fullName;
    private Role role;
}
