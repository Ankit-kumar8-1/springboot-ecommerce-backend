package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Exception.UserNotFoundException;
import com.ankitsaahariya.Service.OrderService;
import com.ankitsaahariya.dao.OrderItemRepository;
import com.ankitsaahariya.dao.OrderRepository;
import com.ankitsaahariya.dao.ProductRepository;
import com.ankitsaahariya.dao.UserRepository;
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

    @Override
    public OrderResponse getOrderById(Long orderId) {
        UserEntity user = getCurrentUser();

        Order order = orderRepository
                .findByIdAndUser_Id(orderId, user.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Order not found or you don't have permission"
                ));

        return mapToDetailResponse(order);
    }


    private OrderResponse mapToDetailResponse(Order order) {
        OrderResponse response = mapToResponse(order);

        // Add address
        if (order.getShippingAddress() != null) {
            response.setShippingAddress(mapAddressToResponse(order.getShippingAddress()));
        }

        // Add order items
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemResponse> itemResponses = items.stream()
                .map(this::mapOrderItemToResponse)
                .toList();
        response.setOrderItems(itemResponses);

        return response;
    }


    private AddressResponse mapAddressToResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setName(address.getName());
        response.setMobile(address.getMobile());
        response.setAddress(address.getAddress());
        response.setLocality(address.getLocality());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setPinCode(address.getPinCode());
        response.setAddressType(address.getAddressType());
        return response;
    }

    private OrderItemResponse mapOrderItemToResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();

        response.setId(item.getId());
        response.setQuantity(item.getQuantity());
        response.setSize(item.getSize());
        response.setMrpPrice(item.getMrpPrice());
        response.setSellingPrice(item.getSellingPrice());
        response.setTotalPrice(item.getSellingPrice() * item.getQuantity());

        // Product info
        if (item.getProduct() != null) {
            Product product = item.getProduct();
            response.setProductId(product.getId());
            response.setProductTitle(product.getTitle());
            response.setColor(product.getColor());

            if (product.getImages() != null && !product.getImages().isEmpty()) {
                response.setProductImage(product.getImages().get(0));
            }

            // Seller info
            if (product.getSeller() != null) {
                response.setSellerId(product.getSeller().getId());
                response.setSellerName(product.getSeller().getBusinessName());
            }
        }

        return response;
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
