package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Exception.BadRequestException;
import com.ankitsaahariya.Exception.ResourceNotFoundException;
import com.ankitsaahariya.Exception.UserNotFoundException;
import com.ankitsaahariya.Service.CartService;
import com.ankitsaahariya.dao.*;
import com.ankitsaahariya.dto.request.AddToCartRequest;
import com.ankitsaahariya.dto.request.ApplyCouponRequest;
import com.ankitsaahariya.dto.request.UpdateQuantityRequest;
import com.ankitsaahariya.dto.response.CartItemResponse;
import com.ankitsaahariya.dto.response.CartResponse;
import com.ankitsaahariya.dto.response.MessageResponse;
import com.ankitsaahariya.entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    @Transactional
    @Override
    public CartResponse addToCart(AddToCartRequest request) {
        UserEntity user = getCurrentUser();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(()-> new ResourceNotFoundException("Product Not found With this Id !"));

        if (product.getQuantity() <= 0){
            throw new BadRequestException("Product is Out of Stock !");
        }

        if (request.getQuantity() > product.getQuantity()){
            throw new ResourceNotFoundException("only "+ product.getQuantity() +" items available in stock");
        }

        Cart cart = cartRepository.findByUserId(user.getId()).
                orElseGet(()-> createNewCart(user));

        Optional<CartItem> existingItem;
        if (request.getSize() != null && !request.getSize().isBlank()) {
            existingItem = cartItemRepository.findByCartIdAndProductIdAndSize(
                    cart.getId(),
                    product.getId(),
                    request.getSize());
        } else {
            existingItem = cartItemRepository.findByCartIdAndProductId(
                    cart.getId(),
                    product.getId());
        }

        if(existingItem.isPresent()) {
            // Update existing item quantity
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + request.getQuantity();

            if (newQuantity > product.getQuantity()) {
                throw new RuntimeException(
                        "Cannot add more. Only " + product.getQuantity() + " items available");
            }

            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        } else {
            // Create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setSize(request.getSize());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setMrpPrice(product.getMrpPrice());
            cartItem.setSellingPrice(product.getSellingPrice());
            cartItem.setUserId(user.getId());

            cart.getCartItems().add(cartItem);
            cartItemRepository.save(cartItem);
        }

        // Recalculate cart totals
        recalculateCart(cart);

        return mapToResponse(cart);
    }

    @Transactional
    @Override
    public CartResponse updateCartItemQuantity(Long cartItemId, UpdateQuantityRequest request) {
        UserEntity user = getCurrentUser();

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(()-> new ResourceNotFoundException("Cart Item not found !"));

        if (!cartItem.getUserId().equals(user.getId())){
            throw new BadRequestException("You can only update your own cart items !");
        }

        Product product = cartItem.getProduct();
        if (request.getQuantity() > product.getQuantity()){
            throw  new BadRequestException("only " + product.getQuantity() + " Items available in stock");
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        Cart cart = cartItem.getCart();
        recalculateCart(cart);

        return mapToResponse(cart);
    }

    @Transactional
    @Override
    public CartResponse removeCartItem(Long cartItemId) {
        UserEntity user = getCurrentUser();

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(()-> new ResourceNotFoundException("Item Not Found With this Id : "+ cartItemId));

        if (!cartItem.getUserId().equals(user.getId())){
            throw new BadRequestException("You can remove only your own cart items !");
        }

        Cart cart = cartItem.getCart();
        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        recalculateCart(cart);
        return mapToResponse(cart);
    }

    @Override
    public CartResponse getCart() {
        UserEntity user = getCurrentUser();

        Optional<Cart> cartOpt = cartRepository.findByUserId(user.getId());
        if(cartOpt.isEmpty()){
            return new CartResponse();

        }
        return mapToResponse(cartOpt.get());
    }

    @Transactional
    @Override
    public MessageResponse clearCart() {
        UserEntity user = getCurrentUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Cart Not found with this User :" +user.getFullName()));

        cart.getCartItems().clear();
        cartItemRepository.deleteByCartId(cart.getId());

        cart.setTotalItem(0);
        cart.setTotalSellingPrice(0);
        cart.setTotalMprPrice(0);
        cart.setDiscount(0);
        cart.setCouponCode(null);

        cartRepository.save(cart);

        return new MessageResponse("Cart cleared successfully !");
    }

    @Transactional
    @Override
    public CartResponse applyCoupon(ApplyCouponRequest request) {
        UserEntity user = getCurrentUser();

        Cart cart = cartRepository.findByUserId(user.getId()).
                orElseThrow(()-> new ResourceNotFoundException("Cart is empty !"));

        if (cart.getCartItems().isEmpty()){
            throw new BadRequestException("Coupon Cannot not apply on empty cart");
        }

        Coupon coupon = couponRepository.findByCode(request.getCouponCode())
                .orElseThrow(()-> new ResourceNotFoundException("Code is invalid !"));

        validateCoupon(coupon,user,cart);

        cart.setCouponCode(coupon.getCode());

        recalculateCart(cart);
        return mapToResponse(cart);
    }

    @Transactional
    @Override
    public CartResponse removeCoupon() {
        UserEntity user = getCurrentUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Cart is empty"));

        cart.setCouponCode(null);
        recalculateCart(cart);

        return mapToResponse(cart);
    }

    private void validateCoupon(Coupon coupon, UserEntity user, Cart cart) {
        if (!coupon.isActive()) {
            throw new RuntimeException("This coupon is no longer active");
        }

        // Check validity dates
        LocalDate today = LocalDate.now();
        if (today.isBefore(coupon.getValidityStartDate())) {
            throw new RuntimeException("Coupon is not yet valid");
        }
        if (today.isAfter(coupon.getValidityEndDate())) {
            throw new RuntimeException("Coupon has expired");
        }

        // Check minimum order value
        if (cart.getTotalSellingPrice() < coupon.getMinimumOrderValue()) {
            throw new RuntimeException(
                    "Minimum order value of ₹" + coupon.getMinimumOrderValue() + " required");
        }

        // Check if user already used this coupon
        if (coupon.getUsedByUsers().contains(user)) {
            throw new RuntimeException("You have already used this coupon");
        }
    }

    private CartResponse mapToResponse(Cart cart) {
        CartResponse response = new CartResponse();

        response.setId(cart.getId());
        response.setTotalItems(cart.getTotalItem());
        response.setTotalMrpPrice(cart.getTotalMprPrice());
        response.setTotalSellingPrice(cart.getTotalSellingPrice());
        response.setDiscount(cart.getDiscount());

        List<CartItemResponse> itemResponses = cart.getCartItems()
                .stream()
                .map(this::mapItemToResponse)
                .toList();
        response.setItems(itemResponses);

        if(cart.getCouponCode() != null){
            response.setCouponCode(cart.getCouponCode());

            Optional<Coupon> couponOpt = couponRepository.findByCode(cart.getCouponCode());
            if (couponOpt.isPresent()){
                Coupon coupon =  couponOpt.get();
                response.setCouponPercentage(coupon.getDiscountPercentage());

                int couponDiscount = (int) Math.round(
                        (cart.getTotalSellingPrice() * coupon.getDiscountPercentage()) / 100);
                response.setCouponDiscount(couponDiscount);
                response.setFinalPrice(cart.getTotalSellingPrice() - couponDiscount);
                response.setTotalSavings(cart.getDiscount() + couponDiscount);
            }else {
                response.setCouponCode(null);
                response.setFinalPrice(cart.getTotalSellingPrice());
                response.setTotalSavings(cart.getDiscount());
            }
        }else {
            response.setFinalPrice(cart.getTotalSellingPrice());
            response.setTotalSavings(cart.getDiscount());
        }

        return response;
    }

    private CartItemResponse mapItemToResponse(CartItem item) {
        CartItemResponse response = new CartItemResponse();

        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setProductTitle(item.getProduct().getTitle());
        response.setColor(item.getProduct().getColor());
        response.setSize(item.getSize());
        response.setQuantity(item.getQuantity());
        response.setMrpPrice(item.getMrpPrice());
        response.setSellingPrice(item.getSellingPrice());

        // Set first product image
        if (item.getProduct().getImages() != null && !item.getProduct().getImages().isEmpty()) {
            response.setProductImage(item.getProduct().getImages().get(0));
        }

        // Calculate item totals
        response.setTotalMrpPrice(item.getMrpPrice() * item.getQuantity());
        response.setTotalSellingPrice(item.getSellingPrice() * item.getQuantity());
        response.setItemDiscount(response.getTotalMrpPrice() - response.getTotalSellingPrice());

        return response;
    }

    private Cart createNewCart(UserEntity user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotalItem(0);
        cart.setTotalMprPrice(0);
        cart.setTotalSellingPrice(0);
        cart.setDiscount(0);
        return cartRepository.save(cart);
    }

    private void recalculateCart(Cart cart) {
        int totalMrp = 0;
        int totalSelling = 0;
        int totalItems = 0;

        // Calculate from items
        for (CartItem item : cart.getCartItems()) {
            totalMrp += item.getMrpPrice() * item.getQuantity();
            totalSelling += item.getSellingPrice() * item.getQuantity();
            totalItems += item.getQuantity();
        }

        cart.setTotalMprPrice(totalMrp);
        cart.setTotalSellingPrice(totalSelling);
        cart.setDiscount(totalMrp - totalSelling);
        cart.setTotalItem(totalItems);

        cartRepository.save(cart);
    }

    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).
                orElseThrow(()-> new UserNotFoundException("User Not Found with this Email : "+ email));
    }
}
