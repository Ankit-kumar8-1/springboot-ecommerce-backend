package com.ankitsaahariya.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private  UserEntity customer;

    @OneToOne
    private SellerProfile seller;

    private LocalDateTime date = LocalDateTime.now();

}
