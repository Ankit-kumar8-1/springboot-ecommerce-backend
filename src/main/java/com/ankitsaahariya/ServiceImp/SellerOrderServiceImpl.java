package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Service.SellerOrderService;
import com.ankitsaahariya.dao.OrderItemRepository;
import com.ankitsaahariya.dao.OrderRepository;
import com.ankitsaahariya.dao.SellerProfileRepository;
import com.ankitsaahariya.dao.UserRepository;
import com.ankitsaahariya.domain.OrderStatus;
import com.ankitsaahariya.dto.request.UpdateOrderStatusRequest;
import com.ankitsaahariya.dto.response.AddressResponse;
import com.ankitsaahariya.dto.response.OrderItemResponse;
import com.ankitsaahariya.dto.response.OrderResponse;
import com.ankitsaahariya.entities.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SellerOrderServiceImpl implements SellerOrderService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;

    @Override
    public Page<OrderResponse> getSellerOrders(Pageable pageable, OrderStatus status) {

        SellerProfile seller = getCurrentSeller();

        Page<Order> ordersPage = (status != null)
                ? orderRepository.findBySeller_IdAndOrderStatusOrderByOrderDateDesc(seller.getId(), status, pageable)
                : orderRepository.findBySeller_IdOrderByOrderDateDesc(seller.getId(), pageable);

        return ordersPage.map(this::mapToResponse);
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {

        SellerProfile seller = getCurrentSeller();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException("You don't have permission to view this order");
        }

        return mapToDetailResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        SellerProfile seller = getCurrentSeller();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Validate ownership
        if (!order.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException("You can only update your own orders");
        }

        // Validate status transition
        validateStatusTransition(order.getOrderStatus(), request.getOrderStatus());

        // Update status
        order.setOrderStatus(request.getOrderStatus());
        Order updatedOrder = orderRepository.save(order);

//        for future
        // emailService.sendOrderStatusUpdate(order.getUser().getEmail(), order);

        return mapToResponse(updatedOrder);
    }

    @Override
    public Map<String, Object> getSellerOrderStats() {
        SellerProfile seller = getCurrentSeller();
        Map<String, Object> stats = new HashMap<>();

        // Total orders
        Long pendingOrders = orderRepository.countBySeller_IdAndOrderStatus(
                seller.getId(),
                OrderStatus.PENDING
        );
        Long confirmedOrders = orderRepository.countBySeller_IdAndOrderStatus(
                seller.getId(),
                OrderStatus.CONFIRMED
        );
        Long shippedOrders = orderRepository.countBySeller_IdAndOrderStatus(
                seller.getId(),
                OrderStatus.SHIPPED
        );
        Long deliveredOrders = orderRepository.countBySeller_IdAndOrderStatus(
                seller.getId(),
                OrderStatus.DELIVERED
        );
        Long cancelledOrders = orderRepository.countBySeller_IdAndOrderStatus(
                seller.getId(),
                OrderStatus.CANCELLED
        );

        stats.put("pendingOrders", pendingOrders);
        stats.put("confirmedOrders", confirmedOrders);
        stats.put("shippedOrders", shippedOrders);
        stats.put("deliveredOrders", deliveredOrders);
        stats.put("cancelledOrders", cancelledOrders);
        stats.put("totalOrders", pendingOrders + confirmedOrders + shippedOrders + deliveredOrders + cancelledOrders);

        // Total revenue (only delivered orders)
        Long totalRevenue = orderRepository.getTotalRevenueBySellerAndStatus(
                seller.getId(),
                OrderStatus.DELIVERED
        );
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0L);

        return stats;
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Define valid transitions
        switch (currentStatus) {
            case PENDING:
                if (newStatus != OrderStatus.CONFIRMED && newStatus != OrderStatus.CANCELLED) {
                    throw new RuntimeException(
                            "Can only move from PENDING to CONFIRMED or CANCELLED"
                    );
                }
                break;

            case CONFIRMED:
                if (newStatus != OrderStatus.PACKED && newStatus != OrderStatus.CANCELLED) {
                    throw new RuntimeException(
                            "Can only move from CONFIRMED to PACKED or CANCELLED"
                    );
                }
                break;

            case PACKED:
                if (newStatus != OrderStatus.SHIPPED) {
                    throw new RuntimeException("Can only move from PACKED to SHIPPED");
                }
                break;

            case SHIPPED:
                if (newStatus != OrderStatus.OUT_FOR_DELIVERY) {
                    throw new RuntimeException(
                            "Can only move from SHIPPED to OUT_FOR_DELIVERY"
                    );
                }
                break;

            case OUT_FOR_DELIVERY:
                if (newStatus != OrderStatus.DELIVERED) {
                    throw new RuntimeException(
                            "Can only move from OUT_FOR_DELIVERY to DELIVERED"
                    );
                }
                break;

            case DELIVERED:
                throw new RuntimeException("Cannot change status of delivered order");

            case CANCELLED:
                throw new RuntimeException("Cannot change status of cancelled order");
        }
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

        mapUser(order, response);
        mapSeller(order, response);
        mapPayment(order, response);

        // Light item mapping (for listing API)
        response.setOrderItems(
                order.getOrderItems().stream()
                        .map(this::mapOrderItemToResponse)
                        .toList()
        );

        return response;
    }

    private OrderResponse mapToDetailResponse(Order order) {

        OrderResponse response = mapToResponse(order);

        // Address mapping only in detail view
        if (order.getShippingAddress() != null) {
            response.setShippingAddress(mapAddress(order.getShippingAddress()));
        }

        // Fetch full items for detail view
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        response.setOrderItems(items.stream()
                .map(this::mapOrderItemToResponse)
                .toList());

        return response;
    }



    private OrderItemResponse mapOrderItemToResponse(OrderItem item) {

        OrderItemResponse dto = new OrderItemResponse();

        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setSize(item.getSize());
        dto.setMrpPrice(item.getMrpPrice());
        dto.setSellingPrice(item.getSellingPrice());
        dto.setTotalPrice(item.getSellingPrice() * item.getQuantity());

        Product product = item.getProduct();
        if (product != null) {
            dto.setProductId(product.getId());
            dto.setProductTitle(product.getTitle());
            dto.setColor(product.getColor());

            if (product.getImages() != null && !product.getImages().isEmpty()) {
                dto.setProductImage(product.getImages().get(0));
            }

            if (product.getSeller() != null) {
                dto.setSellerId(product.getSeller().getId());
                dto.setSellerName(product.getSeller().getBusinessName());
            }
        }

        return dto;
    }



    private void mapUser(Order order, OrderResponse response) {
        if (order.getUser() != null) {
            response.setUserId(order.getUser().getId());
            response.setUserEmail(order.getUser().getEmail());
            response.setUserName(order.getUser().getFullName());
        }
    }

    private void mapSeller(Order order, OrderResponse response) {
        if (order.getSeller() != null) {
            response.setSellerId(order.getSeller().getId());
            response.setSellerBusinessName(order.getSeller().getBusinessName());
        }
    }

    private void mapPayment(Order order, OrderResponse response) {
        if (order.getPaymentDetails() != null) {
            response.setPaymentMethod(order.getPaymentDetails().getPaymentMethod());
            response.setPaymentStatus(order.getPaymentDetails().getPaymentStatus());
            response.setPaymentId(order.getPaymentDetails().getPaymentId());
        }
    }

    private AddressResponse mapAddress(Address address) {
        AddressResponse res = new AddressResponse();
        res.setId(address.getId());
        res.setName(address.getName());
        res.setMobile(address.getMobile());
        res.setAddress(address.getAddress());
        res.setLocality(address.getLocality());
        res.setCity(address.getCity());
        res.setState(address.getState());
        res.setPinCode(address.getPinCode());
        res.setAddressType(address.getAddressType());
        return res;
    }



    private SellerProfile getCurrentSeller() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return sellerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Seller profile not found"));
    }
}

