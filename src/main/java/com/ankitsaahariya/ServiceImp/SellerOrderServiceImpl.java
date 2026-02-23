package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Service.SellerOrderService;
import com.ankitsaahariya.dao.OrderRepository;
import com.ankitsaahariya.dao.SellerProfileRepository;
import com.ankitsaahariya.dao.UserRepository;
import com.ankitsaahariya.domain.OrderStatus;
import com.ankitsaahariya.dto.response.OrderItemResponse;
import com.ankitsaahariya.dto.response.OrderResponse;
import com.ankitsaahariya.entities.Order;
import com.ankitsaahariya.entities.SellerProfile;
import com.ankitsaahariya.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerOrderServiceImpl implements SellerOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;

    @Override
    public Page<OrderResponse> getSellerOrders(Pageable pageable, OrderStatus status) {

        SellerProfile seller = getCurrentSeller();

        Page<Order> ordersPage;

        if (status != null) {
            ordersPage = orderRepository
                    .findBySeller_IdAndOrderStatusOrderByOrderDateDesc(
                            seller.getId(),
                            status,
                            pageable
                    );
        } else {
            ordersPage = orderRepository
                    .findBySeller_IdOrderByOrderDateDesc(
                            seller.getId(),
                            pageable
                    );
        }

        return ordersPage.map(this::mapToResponse);
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

        // USER INFO
        if (order.getUser() != null) {
            response.setUserId(order.getUser().getId());
            response.setUserEmail(order.getUser().getEmail());
            response.setUserName(order.getUser().getFullName());
        }

        // SELLER INFO ✅ FIXED
        if (order.getSeller() != null) {
            response.setSellerId(order.getSeller().getId());
            response.setSellerBusinessName(order.getSeller().getBusinessName());
        }

        // PAYMENT INFO
        if (order.getPaymentDetails() != null) {
            response.setPaymentMethod(order.getPaymentDetails().getPaymentMethod());
            response.setPaymentStatus(order.getPaymentDetails().getPaymentStatus());
            response.setPaymentId(order.getPaymentDetails().getPaymentId());
        }

        // ORDER ITEMS ✅ FIXED
        response.setOrderItems(
                order.getOrderItems()
                        .stream()
                        .map(item -> {

                            OrderItemResponse dto = new OrderItemResponse();

                            dto.setId(item.getId());

                            // PRODUCT INFO
                            if (item.getProduct() != null) {
                                dto.setProductId(item.getProduct().getId());
                                dto.setProductTitle(item.getProduct().getTitle());
                                dto.setProductImage(item.getProduct().getImages().get(0));
                            }

                            // SELLER INFO
                            if (item.getProduct() != null && item.getProduct().getSeller() != null) {

                                dto.setSellerId(item.getProduct().getSeller().getId());
                                dto.setSellerName(item.getProduct().getSeller().getBusinessName());
                            }

                            // PRICE INFO
                            dto.setQuantity(item.getQuantity());
                            dto.setMrpPrice(item.getMrpPrice());
                            dto.setSellingPrice(item.getSellingPrice());

                            // TOTAL PRICE
                            dto.setTotalPrice(item.getSellingPrice() * item.getQuantity());


                            return dto;

                        })
                        .toList()
        );

        return response;
    }

    private SellerProfile getCurrentSeller() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SellerProfile seller = sellerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Seller profile not found"));

        return seller;
    }
}
