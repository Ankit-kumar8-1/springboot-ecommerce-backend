package com.ankitsaahariya.controller;

import com.ankitsaahariya.Service.CartService;
import com.ankitsaahariya.dto.request.AddToCartRequest;
import com.ankitsaahariya.dto.response.CartResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
