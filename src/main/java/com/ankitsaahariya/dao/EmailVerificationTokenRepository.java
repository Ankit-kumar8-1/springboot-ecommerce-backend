package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.EmailVerificationToken;
import com.ankitsaahariya.entities.UserEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerificationTokenRepository  extends JpaRepository<EmailVerificationToken,Long> {

    @Query("""
   select t from EmailVerificationToken t
   where t.user.id = :userId
   and t.used = false
   and t.expiryTime > :now
""")
    Optional<EmailVerificationToken> findActiveToken(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now
    );

    Optional<EmailVerificationToken> findByToken(String token);
}
