package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository  extends JpaRepository<Order,Long> {
}
