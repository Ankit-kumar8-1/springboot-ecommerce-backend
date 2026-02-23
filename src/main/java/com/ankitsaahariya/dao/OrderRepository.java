package com.ankitsaahariya.dao;

import com.ankitsaahariya.domain.OrderStatus;
import com.ankitsaahariya.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository  extends JpaRepository<Order,Long> {

    Page<Order> findBySeller_IdAndOrderStatusOrderByOrderDateDesc(
            Long sellerId,
            OrderStatus status,
            Pageable pageable
    );

    Page<Order> findBySeller_IdOrderByOrderDateDesc(
            Long sellerId,
            Pageable pageable
    );
}
