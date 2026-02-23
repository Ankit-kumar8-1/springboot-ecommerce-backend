package com.ankitsaahariya.dao;

import com.ankitsaahariya.domain.OrderStatus;
import com.ankitsaahariya.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // SELLER ORDERS PAGINATION
    Page<Order> findBySeller_IdAndOrderStatusOrderByOrderDateDesc(
            Long sellerId,
            OrderStatus status,
            Pageable pageable
    );

    Page<Order> findBySeller_IdOrderByOrderDateDesc(
            Long sellerId,
            Pageable pageable
    );

    // COUNT ORDERS BY STATUS
    Long countBySeller_IdAndOrderStatus(Long sellerId, OrderStatus orderStatus);

    // TOTAL REVENUE
    @Query("""
        SELECT SUM(o.totalSellingPrice)
        FROM Order o
        WHERE o.seller.id = :sellerId
        AND o.orderStatus = :status
    """)
    Long getTotalRevenueBySellerAndStatus(
            @Param("sellerId") Long sellerId,
            @Param("status") OrderStatus status
    );

    Page<Order> findByUser_IdOrderByOrderDateDesc(
            Long userId,
            Pageable pageable
    );

    Optional<Order> findByIdAndUser_Id(Long orderId, Long userId);

    Optional<Order> findByOrderId(String orderId);
}
