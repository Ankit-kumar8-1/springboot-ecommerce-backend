package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Exception.UserNotFoundException;
import com.ankitsaahariya.Service.UserService;
import com.ankitsaahariya.dao.UserRepository;
import com.ankitsaahariya.dto.response.UserResponse;
import com.ankitsaahariya.entities.UserEntity;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User Not found with this Email !"));

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .emailVerified(user.isEmailVerified())
                .role(user.getRole())
                .addresses(user.getAddresses().stream().toList())
                .couponSet(user.getUsedCoupons())
                .mobilNumber(user.getMobilNumber())
                .build();
    }
}
