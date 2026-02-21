package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder,Long> {
}
