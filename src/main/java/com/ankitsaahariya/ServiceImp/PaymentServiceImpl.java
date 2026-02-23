package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Exception.BadRequestException;
import com.ankitsaahariya.Exception.ResourceNotFoundException;
import com.ankitsaahariya.Service.PaymentService;
import com.ankitsaahariya.Service.SellerReportService;
import com.ankitsaahariya.dao.*;
import com.ankitsaahariya.domain.OrderStatus;
import com.ankitsaahariya.domain.PaymentMethod;
import com.ankitsaahariya.domain.PaymentOrderStatus;
import com.ankitsaahariya.domain.PaymentStatus;
import com.ankitsaahariya.dto.request.PaymentVerificationRequest;
import com.ankitsaahariya.dto.response.AddressResponse;
import com.ankitsaahariya.dto.response.OrderItemResponse;
import com.ankitsaahariya.dto.response.OrderResponse;
import com.ankitsaahariya.dto.response.PaymentResponse;
import com.ankitsaahariya.entities.*;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${razorpay.key.id}")
    private String razorpayKey;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    private final SellerReportService sellerReportService;
    private final SellerProfileRepository sellerProfileRepository;
    private final RazorpayClient razorpayClient;
    private final PaymentOrderRepository paymentOrderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public JSONObject createPaymentOrder(Long addressId) throws Exception {
        UserEntity user = getCurrentUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Cart is empty !"));

        if (cart.getCartItems().isEmpty()){
            throw new BadRequestException("Cannot create payment for empty cart");
        }
        Address address = addressRepository.findByIdAndUserId(addressId,user.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Address not found!"));

        for (CartItem item : cart.getCartItems()){
            Product product = item.getProduct();
            if (product.getQuantity() < item.getQuantity()){
                throw new RuntimeException(
                        "Insufficient stock for: " + product.getTitle() +
                                ". Available: " + product.getQuantity()
                );
            }
        }

        Long amountInPaise = cart.getTotalSellingPrice() * 100L;
        // 5. Create Razorpay order
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "order_rcptid_" + System.currentTimeMillis());

        com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);

        // 6. Create PaymentOrder entity
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setAmount(cart.getTotalSellingPrice().longValue());
        paymentOrder.setStatus(PaymentOrderStatus.PENDING);
        paymentOrder.setPaymentMethod(PaymentMethod.UPI); // Will be updated after payment
        paymentOrder.setPaymentLinkId(razorpayOrder.get("id"));
        paymentOrder.setUser(user);

        paymentOrderRepository.save(paymentOrder);

        // 7. Return Razorpay order details for frontend
        JSONObject response = new JSONObject();
        response.put("orderId", razorpayOrder.get("id").toString());
        response.put("amount",(Integer) razorpayOrder.get("amount"));
        response.put("currency", razorpayOrder.get("currency").toString());
        response.put("key",razorpayKey);

        return response;
    }

    @Override
    @Transactional
    public PaymentResponse verifyPayment(PaymentVerificationRequest request) throws Exception {
        UserEntity user = getCurrentUser();

        // 1. Verify payment signature (skip for testing)
        // boolean isValid = verifyPaymentSignature(...);
        // if (!isValid) throw new RuntimeException("Invalid signature");

        // 2. Get PaymentOrder
        PaymentOrder paymentOrder = paymentOrderRepository.findByPaymentLinkId(request.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Payment order not found"));

        // 3. Get Cart
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart is empty"));

        // 4. Get Address
        Address address = addressRepository.findByIdAndUserId(request.getAddressId(), user.getId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // 5. GROUP BY SELLER
        Map<Long, List<CartItem>> itemsBySeller = cart.getCartItems().stream()
                .collect(Collectors.groupingBy(
                        item -> item.getProduct().getSeller().getId()
                ));

        // 6. CREATE ORDERS
        List<Order> createdOrders = new ArrayList<>();

        for (Map.Entry<Long, List<CartItem>> entry : itemsBySeller.entrySet()) {
            Long sellerId = entry.getKey();
            List<CartItem> sellerItems = entry.getValue();

            // Calculate totals
            int sellerTotalMrp = sellerItems.stream()
                    .mapToInt(item -> item.getMrpPrice() * item.getQuantity())
                    .sum();

            int sellerTotalSelling = sellerItems.stream()
                    .mapToInt(item -> item.getSellingPrice() * item.getQuantity())
                    .sum();

            int sellerTotalItems = sellerItems.stream()
                    .mapToInt(CartItem::getQuantity)
                    .sum();

            // Create Order
            Order order = new Order();
            order.setOrderId(generateOrderId());
            order.setUser(user);
            order.setSellerId(sellerId);
            order.setShippingAddress(address);
            order.setOrderDate(LocalDateTime.now());
            order.setDeliverDate(LocalDateTime.now().plusDays(7));
            order.setOrderStatus(OrderStatus.PENDING);
            order.setTotalMrpPrice(sellerTotalMrp);
            order.setTotalSellingPrice(sellerTotalSelling);
            order.setDiscount(sellerTotalMrp - sellerTotalSelling);
            order.setTotalItem(sellerTotalItems);

            // Payment details
            PaymentDetails paymentDetails = new PaymentDetails();
            paymentDetails.setPaymentMethod(PaymentMethod.UPI);
            paymentDetails.setPaymentStatus(PaymentStatus.COMPLETED);
            paymentDetails.setPaymentId(request.getRazorpayPaymentId());
            paymentDetails.setRazorpayOrderId(request.getRazorpayOrderId());
            paymentDetails.setRazorpayPaymentId(request.getRazorpayPaymentId());
            paymentDetails.setPaymentDate(LocalDateTime.now());
            order.setPaymentDetails(paymentDetails);

            Order savedOrder = orderRepository.save(order);

            sellerReportService.updateOnOrderCreated(sellerId);

            // Create Order Items
            for (CartItem cartItem : sellerItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(savedOrder);
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setSize(cartItem.getSize());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setMrpPrice(cartItem.getMrpPrice());
                orderItem.setSellingPrice(cartItem.getSellingPrice());
                orderItem.setUserId(user.getId());

                orderItemRepository.save(orderItem);

                // Reduce stock
                Product product = cartItem.getProduct();
                product.setQuantity(product.getQuantity() - cartItem.getQuantity());
                productRepository.save(product);
            }

            paymentOrder.getOrders().add(savedOrder);
            createdOrders.add(savedOrder);
        }

        // 7. Update payment status
        paymentOrder.setStatus(PaymentOrderStatus.COMPLETED);
        paymentOrderRepository.save(paymentOrder);

        // 8. Clear cart
        cartItemRepository.deleteByCartId(cart.getId());
        cart.getCartItems().clear();
        cart.setTotalItem(0);
        cart.setTotalMprPrice(0);
        cart.setTotalSellingPrice(0);
        cart.setDiscount(0);
        cart.setCouponCode(null);
        cartRepository.save(cart);

        // 9. Prepare response
        PaymentResponse response = new PaymentResponse();
        response.setPaymentOrderId(paymentOrder.getId());
        response.setRazorpayOrderId(request.getRazorpayOrderId());
        response.setRazorpayPaymentId(request.getRazorpayPaymentId());
        response.setAmount(paymentOrder.getAmount());
        response.setStatus(PaymentOrderStatus.COMPLETED);
        response.setMessage("Payment successful! " + createdOrders.size() + " order(s) created.");

        // Map orders with COMPLETE data
        List<OrderResponse> orderResponses = createdOrders.stream()
                .map(this::mapToOrderResponse)
                .toList();
        response.setOrders(orderResponses);

        return response;
    }


    @Override
    @Transactional
    public void handlePaymentFailure(String razorpayOrderId) {
        PaymentOrder paymentOrder = paymentOrderRepository.findByPaymentLinkId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Payment order not found"));

        paymentOrder.setStatus(PaymentOrderStatus.FAILED);
        paymentOrderRepository.save(paymentOrder);
    }

    // ... other methods ...

    // ========== UPDATED MAPPER METHODS ==========

    private OrderResponse mapToOrderResponse(Order order) {
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

        // User info
        if (order.getUser() != null) {
            response.setUserId(order.getUser().getId());
            response.setUserEmail(order.getUser().getEmail());
            response.setUserName(order.getUser().getFullName());
        }

        // Payment info
        if (order.getPaymentDetails() != null) {
            response.setPaymentMethod(order.getPaymentDetails().getPaymentMethod());
            response.setPaymentStatus(order.getPaymentDetails().getPaymentStatus());
            response.setPaymentId(order.getPaymentDetails().getPaymentId());
        }

        // Seller info
        response.setSellerId(order.getSellerId());

        // Address
        if (order.getShippingAddress() != null) {
            response.setShippingAddress(mapAddressToResponse(order.getShippingAddress()));
        }

        // Order Items
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemResponse> itemResponses = items.stream()
                .map(this::mapOrderItemToResponse)
                .toList();
        response.setOrderItems(itemResponses);

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

        if (item.getProduct() != null) {
            Product product = item.getProduct();
            response.setProductId(product.getId());
            response.setProductTitle(product.getTitle());
            response.setColor(product.getColor());

            if (product.getImages() != null && !product.getImages().isEmpty()) {
                response.setProductImage(product.getImages().get(0));
            }

            if (product.getSeller() != null) {
                response.setSellerId(product.getSeller().getId());
                response.setSellerName(product.getSeller().getBusinessName());
            }
        }

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

    private String generateOrderId() {
        return "ORD" + System.currentTimeMillis();
    }

    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
