package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Exception.UserNotFoundException;
import com.ankitsaahariya.Service.OrderService;
import com.ankitsaahariya.dao.OrderItemRepository;
import com.ankitsaahariya.dao.OrderRepository;
import com.ankitsaahariya.dao.ProductRepository;
import com.ankitsaahariya.dao.UserRepository;
import com.ankitsaahariya.dto.response.OrderResponse;
import com.ankitsaahariya.entities.Order;
import com.ankitsaahariya.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    public Page<OrderResponse> getUserOrders(Pageable pageable) {
        UserEntity user = getCurrentUser();

        Page<Order> orderPage  = orderRepository.findByUser_IdOrderByOrderDateDesc(user.getId(),pageable);

        return orderPage.map(this::mapToResponse);
    }

    private OrderResponse mapToResponse(Order order) {

        OrderResponse response = new OrderResponse();

        response.setId(order.getId());
        response.setOrderId(order.getOrderId());
        response.setOrderStatus(order.getOrderStatus());
        response.setOrderDate(order.getOrderDate());
        response.setDeliverDate(order.getDeliverDate());
        response.setTotalItem(order.getTotalItem());
        response.setTotalMrpPrice(order.getTotalMrpPrice());
        response.setTotalSellingPrice(order.getTotalSellingPrice());
        response.setDiscount(order.getDiscount());


        if (order.getUser() != null) {
            response.setUserId(order.getUser().getId());
            response.setUserEmail(order.getUser().getEmail());
            response.setUserName(order.getUser().getFullName());
        }


        if (order.getPaymentDetails() != null) {
            response.setPaymentMethod(order.getPaymentDetails().getPaymentMethod());
            response.setPaymentStatus(order.getPaymentDetails().getPaymentStatus());
            response.setPaymentId(order.getPaymentDetails().getPaymentId());
        }


        if (order.getSeller() != null) {
            response.setSellerId(order.getSeller().getId());
            response.setSellerBusinessName(order.getSeller().getBusinessName());
        }

        return response;
    }

    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("User not found with this Email : "+ email));
    }
}
