package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.EmailVerificationToken;
import com.ankitsaahariya.entities.UserEntity;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(@NotBlank(message = "Email is required ") @Email(message = "Invalid Email formate") String email);


}
