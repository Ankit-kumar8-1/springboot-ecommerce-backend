package com.ankitsaahariya.controller;


import com.ankitsaahariya.Service.SellerService;
import com.ankitsaahariya.dto.request.SellerApplicationRequest;
import com.ankitsaahariya.dto.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {
    private final SellerService sellerService;

    @PostMapping("/requestSellerIntent")
    public ResponseEntity<MessageResponse>  requestSellerIntent(Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(sellerService.requestSellerIntent(email));
    }

    @GetMapping("/verify-Seller-Intent")
    public ResponseEntity<MessageResponse> verifySellerIntent(@RequestParam String token){
        return ResponseEntity.ok(sellerService.verifySellerIntent(token));
    }


}
