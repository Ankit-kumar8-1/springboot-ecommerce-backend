package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.PaymentOrder;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder,Long> {

    Optional<PaymentOrder> findByPaymentLinkId( String paymentLinkId);
}
