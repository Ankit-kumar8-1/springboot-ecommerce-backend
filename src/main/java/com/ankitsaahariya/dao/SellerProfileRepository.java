package com.ankitsaahariya.dao;

import com.ankitsaahariya.domain.SellerVerificationStatus;
import com.ankitsaahariya.entities.SellerProfile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerProfileRepository extends JpaRepository<SellerProfile,Long> {

    Optional<SellerProfile> findByUserId(Long id);

    boolean existsByGstNumber(String gstNumber);

    Page<SellerProfile> findAllByVerificationStatus(
            SellerVerificationStatus status,
            Pageable pageable
    );
}
