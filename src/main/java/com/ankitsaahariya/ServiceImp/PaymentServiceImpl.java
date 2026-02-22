package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Exception.BadRequestException;
import com.ankitsaahariya.Exception.ResourceNotFoundException;
import com.ankitsaahariya.Exception.UserNotFoundException;
import com.ankitsaahariya.Service.PaymentService;
import com.ankitsaahariya.configuration.RazorpayConfig;
import com.ankitsaahariya.dao.*;
import com.ankitsaahariya.domain.OrderStatus;
import com.ankitsaahariya.domain.PaymentMethod;
import com.ankitsaahariya.domain.PaymentOrderStatus;
import com.ankitsaahariya.domain.PaymentStatus;
import com.ankitsaahariya.dto.request.PaymentVerificationRequest;
import com.ankitsaahariya.dto.response.OrderResponse;
import com.ankitsaahariya.dto.response.PaymentResponse;
import com.ankitsaahariya.entities.*;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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

    @Transactional
    @Override
    public PaymentResponse verifyPayment(PaymentVerificationRequest request) throws Exception {
        UserEntity user = getCurrentUser();

//        boolean isValid = verifyPaymentSignature(
//                request.getRazorpayOrderId(),
//                request.getRazorpayPaymentId(),
//                request.getRazorpaySignature()
//        );
//        if (!isValid){
//            throw new RuntimeException("Invalid payment signature. Payment verification failed.");
//        }
        PaymentOrder paymentOrder = paymentOrderRepository.findByPaymentLinkId(request.getRazorpayOrderId())
                .orElseThrow(()-> new RuntimeException("Payment order not found !"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Cart is empty"));

        Address address = addressRepository.findByIdAndUserId(request.getAddressId(),user.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Address not found "));

        Map<Long, List<CartItem>> itemsBySeller = cart.getCartItems().stream()
                .collect(Collectors.groupingBy(
                        item -> item.getProduct().getSeller().getId()
                ));

        List<Order> createdOrders = new ArrayList<>();

        for (Map.Entry<Long, List<CartItem>> entry : itemsBySeller.entrySet()) {
            Long sellerId = entry.getKey();
            List<CartItem> sellerItems = entry.getValue();

            // Calculate totals for this seller
            int sellerTotalMrp = sellerItems.stream()
                    .mapToInt(item -> item.getMrpPrice() * item.getQuantity())
                    .sum();

            int sellerTotalSelling = sellerItems.stream()
                    .mapToInt(item -> item.getSellingPrice() * item.getQuantity())
                    .sum();

            int sellerTotalItems = sellerItems.stream()
                    .mapToInt(CartItem::getQuantity)
                    .sum();

            // Create Order for this seller
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

            // Set payment details
            PaymentDetails paymentDetails = new PaymentDetails();
            paymentDetails.setPaymentMethod(PaymentMethod.UPI);
            paymentDetails.setPaymentStatus(PaymentStatus.COMPLETED);
            paymentDetails.setPaymentId(request.getRazorpayPaymentId());
            paymentDetails.setRazorpayOrderId(request.getRazorpayOrderId());
            paymentDetails.setRazorpayPaymentId(request.getRazorpayPaymentId());
            paymentDetails.setPaymentDate(LocalDateTime.now());
            order.setPaymentDetails(paymentDetails);

            Order savedOrder = orderRepository.save(order);

            // Create Order Items for this seller
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

                // Reduce product stock
                Product product = cartItem.getProduct();
                product.setQuantity(product.getQuantity() - cartItem.getQuantity());
                productRepository.save(product);
            }

            // Link order to payment order
            paymentOrder.getOrders().add(savedOrder);
            createdOrders.add(savedOrder);

            // Create Transaction record
            Transaction transaction = new Transaction();
            transaction.setCustomer(user);
            transaction.setSeller(sellerItems.get(0).getProduct().getSeller());
            transaction.setDate(LocalDateTime.now());
            transactionRepository.save(transaction);
        }

        // 7. Update PaymentOrder status
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

        // Add order responses
        List<OrderResponse> orderResponses = createdOrders.stream()
                .map(this::mapToOrderResponse)
                .toList();
        response.setOrders(orderResponses);

        return response;

    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderId(order.getOrderId());
        response.setOrderStatus(order.getOrderStatus());
        response.setTotalItem(order.getTotalItem());
        response.setTotalMrpPrice(order.getTotalMrpPrice());
        response.setTotalSellingPrice(order.getTotalSellingPrice());
        response.setDiscount(order.getDiscount());
        response.setOrderDate(order.getOrderDate());
        response.setDeliverDate(order.getDeliverDate());
        return response;
    }

    private String generateOrderId() {
        return  "ORD"+System.currentTimeMillis();
    }

    private boolean verifyPaymentSignature( String orderId,  String paymentId, String signature) {
        try{
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", signature);
            return Utils.verifyPaymentSignature(options,razorpayKeySecret);
        }catch (Exception e){
            return false;
        }
    }

    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("User not found with email : "+ email));
    }
}
