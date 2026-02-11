package com.ankitsaahariya.entities;

import com.ankitsaahariya.domain.SellerIntentTokenStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "seller_intent_tokens",
        indexes = {
                @Index(name = "idx_seller_intent_token", columnList = "token"),
                @Index(name = "idx_seller_intent_user", columnList = "user_id")
        }
)
public class SellerIntentToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true, length = 100)
    private String token;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SellerIntentTokenStatus status = SellerIntentTokenStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime usedAt;

    @Column(updatable = false)
    private LocalDateTime createdAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public boolean isVerified() {
        return this.status == SellerIntentTokenStatus.VERIFIED;
    }
}
