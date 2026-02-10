package com.ankitsaahariya.entities;


import com.ankitsaahariya.domain.AccountStatus;
import com.ankitsaahariya.domain.BusinessType;
import com.ankitsaahariya.domain.Role;
import com.ankitsaahariya.domain.SellerVerificationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NotNull
@Builder
@Table(name = "seller_profiles")
public class SellerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= USER =================
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SellerVerificationStatus verificationStatus;

//     BUSINESS DETAILS
    private String businessName;

    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @Column(length = 500)
    private String businessAddress;

    private String businessCity;
    private String businessState;
    private String businessPincode;
    private String businessPhone;
    private String businessEmail;

    @Column(length = 1000)
    private String businessDescription;

    //LEGAL DETAILS
    @Column(length = 20)
    private String gstNumber;

    @Column(length = 15)
    private String panNumber;

    @Column(length = 12)
    private String aadharNumber;

    //  BANK DETAILS
    private String bankAccountNumber;
    private String bankIfscCode;
    private String bankAccountHolderName;
    private String bankName;
    private String bankBranch;

    // DOCUMENT PATHS
    private String gstCertificatePath;
    private String panCardPath;
    private String aadharCardPath;
    private String cancelledChequePath;
    private String businessProofPath;

    // ADMIN ACTION
    @Column(length = 1000)
    private String adminRemarks;

    private LocalDateTime appliedAt;
    private LocalDateTime verifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_admin_id")
    private UserEntity verifiedByAdmin;

    //  SELLER METRICS
    private Double sellerRating = 0.0;
    private Integer totalProducts = 0;
    private Integer totalOrders = 0;

    private Boolean isActive = true;

    // EXISTING RELATIONSHIPS
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    @OneToOne(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private SellerReport sellerReport;

    @OneToMany(mappedBy = "seller")
    private List<Transaction> transactions = new ArrayList<>();

    //  TIMESTAMPS
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.appliedAt = LocalDateTime.now();
        this.verificationStatus = SellerVerificationStatus.NOT_APPLIED;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
