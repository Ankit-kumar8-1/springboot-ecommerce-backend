package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.UserEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity>  findByEmail(String email);

    boolean existsByEmail(@NotBlank(message = "Email is required ") @Email(message = "Invalid Email formate") String email);
}
