package com.ankitsaahariya.Service;

import com.ankitsaahariya.domain.OrderStatus;
import com.ankitsaahariya.dto.request.UpdateOrderStatusRequest;
import com.ankitsaahariya.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface SellerOrderService {

    Page<OrderResponse> getSellerOrders(Pageable pageable, OrderStatus status);

    OrderResponse getOrderById(Long orderId);

    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);

    Map<String, Object> getSellerOrderStats();
}
