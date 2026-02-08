package com.ankitsaahariya.entities;

import com.ankitsaahariya.domain.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    private boolean enabled = true;

    private String email;

    private String fullName;

    private String mobilNumber;

    @Column
    private String passwordRestToken;

    @Column
    private Instant passwordRestTokenExpire;

    @Column(name = "password_reset_verified",nullable = false)
    private Boolean passwordResetVerified =false;

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_CUSTOMER;


    private boolean emailVerified = false;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<EmailVerificationToken> verificationTokens  = new ArrayList<>();

    @OneToMany
    private Set<Address> addresses = new HashSet<>();

    @ManyToMany
    @JsonIgnore
    private Set<Coupon> usedCoupons =  new HashSet<>();

}
