package com.ankitsaahariya.controller;


import com.ankitsaahariya.Service.SellerService;
import com.ankitsaahariya.domain.SellerVerificationStatus;
import com.ankitsaahariya.dto.request.SellerApplicationRequest;
import com.ankitsaahariya.dto.response.MessageResponse;
import com.ankitsaahariya.dto.response.PageResponse;
import com.ankitsaahariya.dto.response.SellerProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping("/applyForSeller")
    public ResponseEntity<MessageResponse> applyForSeller(@RequestBody SellerApplicationRequest request){
        return ResponseEntity.ok(sellerService.applyForSeller(request));
    }

    @GetMapping("/admin/getApplication")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<SellerProfileResponse>> getSellerApplications(
            @RequestParam SellerVerificationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                sellerService.getSellerApplications(status, page, size)
        );
    }
}
