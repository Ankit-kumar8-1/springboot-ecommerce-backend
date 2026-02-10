package com.ankitsaahariya.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter@Setter
public class SellerReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private SellerProfile seller;

    private Long totalEarnings = 0L;

    private  Long totalSales = 0L;

    private Long totalRefund = 0L;

    private  Long totalTax = 0L;

    private Long netEarnings = 0L;

    private  Integer totalOrders = 0;

    private Integer canceledOrders = 0;

    private Integer totalTransactions =0;

}
