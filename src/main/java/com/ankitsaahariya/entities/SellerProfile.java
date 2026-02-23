package com.ankitsaahariya.entities;

import com.ankitsaahariya.domain.BusinessType;
import com.ankitsaahariya.domain.SellerVerificationStatus;
import com.ankitsaahariya.entities.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "seller_profiles")
public class SellerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= USER =================
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    // ================= STATUS =================
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SellerVerificationStatus verificationStatus;

    private Boolean isActive = true;

    // ================= BUSINESS DETAILS =================
    @Column(nullable = false)
    private String businessName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusinessType businessType;

    @Column(nullable = false, length = 500)
    private String businessAddress;

    @Column(nullable = false)
    private String businessCity;

    @Column(nullable = false)
    private String businessState;

    @Column(nullable = false)
    private String businessPincode;

    @Column(nullable = false)
    private String businessPhone;

    private String businessEmail;

    @Column(length = 1000)
    private String businessDescription;

    // ================= LEGAL DETAILS =================
    @Column(nullable = false, unique = true, length = 20)
    private String gstNumber;

    @Column(nullable = false, length = 15)
    private String panNumber;

    @Column(length = 12)
    private String aadharNumber;

    // ================= BANK DETAILS =================
    @Column(nullable = false)
    private String bankAccountNumber;

    @Column(nullable = false)
    private String bankIfscCode;

    @Column(nullable = false)
    private String bankAccountHolderName;

    @Column(nullable = false)
    private String bankName;

    private String bankBranch;

    // ================= ADMIN ACTION =================
    @Column(length = 1000)
    private String adminRemarks;

    private LocalDateTime appliedAt;
    private LocalDateTime verifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_admin_id")
    private UserEntity verifiedByAdmin;

    // ================= METRICS =================
    private Double sellerRating = 0.0;
    private Integer totalProducts = 0;
    private Integer totalOrders = 0;

    // ================= RELATIONS =================
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();


    @OneToMany(mappedBy = "seller")
    private List<Transaction> transactions = new ArrayList<>();

    // ================= TIMESTAMPS =================
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.appliedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
