package com.ankitsaahariya.entities;

import com.ankitsaahariya.domain.AddressType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // Recipient name

    private String mobile;  // Contact number

    private String locality;

    private String address;  // Street address

    private String city;

    private String state;

    private String pinCode;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;  // HOME, WORK, OTHER

    private Boolean isDefault = false;  // Default address for delivery

    // User relationship - Critical for ownership
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
