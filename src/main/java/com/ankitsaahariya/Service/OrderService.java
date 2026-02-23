package com.ankitsaahariya.Service;

import com.ankitsaahariya.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    Page<OrderResponse>  getUserOrders(Pageable pageable);

    OrderResponse getOrderById(Long orderId);

    OrderResponse getOrderByOrderId(String orderId);
}

