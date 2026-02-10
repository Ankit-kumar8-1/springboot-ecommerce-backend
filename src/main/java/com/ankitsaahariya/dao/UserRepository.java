package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.EmailVerificationToken;
import com.ankitsaahariya.entities.UserEntity;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(@NotBlank(message = "Email is required ") @Email(message = "Invalid Email formate") String email);


    Optional<UserEntity> findByPasswordRestToken(String token);

    @Query("""
    SELECT u FROM UserEntity u
    WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
       OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
""")
    Page<UserEntity> searchUser(@Param("search") String search, Pageable pageable);
}
