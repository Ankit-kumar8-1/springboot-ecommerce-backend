package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.SellerIntentToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerIntentTokenRepository extends JpaRepository<SellerIntentToken,Long> {

    Optional<SellerIntentToken> findTopByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<SellerIntentToken> findByToken(String token);
}
