package com.ankitsaahariya.entities;


import com.ankitsaahariya.domain.AccountStatus;
import com.ankitsaahariya.domain.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NotNull
@EqualsAndHashCode
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sellerName;

    private String mobil;

    @Column(unique = true,nullable = false)
    private String email;

    private String password;

    @Embedded
    private BusinessDetails businessDetails =  new BusinessDetails();

    @Embedded
    private BankDetails bankDetails = new BankDetails();

    @OneToOne(cascade = CascadeType.ALL)
    private Address  pickupAddress = new Address();

    private String GSTIN;

    private Role role = Role.ROLE_SELLER;

    private boolean isEmailVerified =  false;

    private AccountStatus accountStatus = AccountStatus.PENDING_VERIFICATION;
}
