package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Service.SellerOrderService;
import com.ankitsaahariya.dao.OrderItemRepository;
import com.ankitsaahariya.dao.OrderRepository;
import com.ankitsaahariya.dao.SellerProfileRepository;
import com.ankitsaahariya.dao.UserRepository;
import com.ankitsaahariya.domain.OrderStatus;
import com.ankitsaahariya.dto.response.AddressResponse;
import com.ankitsaahariya.dto.response.OrderItemResponse;
import com.ankitsaahariya.dto.response.OrderResponse;
import com.ankitsaahariya.entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

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

