package com.ankitsaahariya.entities;

import com.ankitsaahariya.domain.PaymentMethod;
import com.ankitsaahariya.domain.PaymentOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;

    @Enumerated(EnumType.STRING)
    private PaymentOrderStatus status = PaymentOrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String paymentLinkId;  // Razorpay order_id

    @ManyToOne
    private UserEntity user;

    @OneToMany(mappedBy = "paymentOrder")  // If using bidirectional
    private Set<Order> orders = new HashSet<>();
}
