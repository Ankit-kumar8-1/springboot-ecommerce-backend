package com.ankitsaahariya.dto.response;

import com.ankitsaahariya.domain.Role;
import com.ankitsaahariya.entities.Address;
import com.ankitsaahariya.entities.Coupon;
import com.ankitsaahariya.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String email;

    private String mobilNumber;

    private String fullName;

    private Role role;

    private boolean emailVerified ;

    private List<Address> addresses = new ArrayList<>();

    private Set<Coupon> couponSet = new HashSet<>();

    public static UserResponse fromEntity(UserEntity user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .mobilNumber(user.getMobilNumber())
                .fullName(user.getFullName())
                .emailVerified(user.isEmailVerified())
                .role(user.getRole())
                .addresses(user.getAddresses().stream().toList())
                .couponSet(user.getUsedCoupons())
                .build();
    }
}
