package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Exception.BadRequestException;
import com.ankitsaahariya.Exception.ResourceNotFoundException;
import com.ankitsaahariya.Exception.UserNotFoundException;
import com.ankitsaahariya.Service.PaymentService;
import com.ankitsaahariya.dao.*;
import com.ankitsaahariya.domain.PaymentMethod;
import com.ankitsaahariya.domain.PaymentOrderStatus;
import com.ankitsaahariya.entities.*;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${razorpay.key.id}")
    private String razorpayKey;

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

    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("User not found with email : "+ email));
    }
}
