package com.ankitsaahariya.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    private String otp;

    private String email;

    @OneToOne
    private UserEntity user;

    @OneToOne
    private SellerProfile seller;

}
