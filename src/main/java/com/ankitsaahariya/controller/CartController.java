package com.ankitsaahariya.controller;

import com.ankitsaahariya.Service.CartService;
import com.ankitsaahariya.dto.request.AddToCartRequest;
import com.ankitsaahariya.dto.request.ApplyCouponRequest;
import com.ankitsaahariya.dto.request.UpdateQuantityRequest;
import com.ankitsaahariya.dto.response.CartResponse;
import com.ankitsaahariya.dto.response.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request
            ){
        return ResponseEntity.ok(cartService.addToCart(request));
    }

    @PutMapping("/item/{cartItemId}/quantity")
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateQuantityRequest request
    ){
        return  ResponseEntity.ok(cartService.updateCartItemQuantity(cartItemId,request));
    }

    @DeleteMapping("/item/remove/{cartItemId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Long cartItemId){
        return ResponseEntity.ok(cartService.removeCartItem(cartItemId));
    }

    @GetMapping("/get")
    public ResponseEntity<CartResponse> getCart(){
        return ResponseEntity.ok(cartService.getCart());
    }

    @DeleteMapping("/clear")
    public ResponseEntity<MessageResponse> clearCart(){
        return  ResponseEntity.ok(cartService.clearCart());
    }

    @PostMapping("/apply-coupon")
    public ResponseEntity<CartResponse> applyCoupon(
            @Valid @RequestBody ApplyCouponRequest request) {
        return ResponseEntity.ok(cartService.applyCoupon(request));
    }

}
