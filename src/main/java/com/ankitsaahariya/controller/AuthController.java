package com.ankitsaahariya.controller;

import com.ankitsaahariya.Service.AuthService;
import com.ankitsaahariya.dto.request.LoginRequest;
import com.ankitsaahariya.dto.request.EmailRequest;
import com.ankitsaahariya.dto.request.SignupRequest;
import com.ankitsaahariya.dto.response.LoginResponse;
import com.ankitsaahariya.dto.response.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signup(@RequestBody  SignupRequest signupRequest){
        return ResponseEntity.ok(authService.signup(signupRequest));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestParam String token){
        return ResponseEntity.ok(authService.verifyEmail(token));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse>  login(@RequestBody LoginRequest loginRequest){
        return  ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/resend-verification")
    public  ResponseEntity<MessageResponse> resendVerificationLink(@RequestBody EmailRequest request){
        return ResponseEntity.ok(authService.resendVerificationLink(request));
    }


    @PostMapping("/forgot-password-request")
    public ResponseEntity<MessageResponse> forgotPasswordRequest(@Valid @RequestBody  EmailRequest request){
        return ResponseEntity.ok(authService.forgotPasswordRequest(request));
    }
}
