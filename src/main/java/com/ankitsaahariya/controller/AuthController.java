package com.ankitsaahariya.controller;

import com.ankitsaahariya.Service.AuthService;
import com.ankitsaahariya.dto.request.*;
import com.ankitsaahariya.dto.response.LoginResponse;
import com.ankitsaahariya.dto.response.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signup( @RequestBody  SignupRequest signupRequest){
        return ResponseEntity.ok(authService.signup(signupRequest));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestParam String token){
        return ResponseEntity.ok(authService.verifyEmail(token));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse>  login(@Valid @RequestBody LoginRequest loginRequest){
        return  ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/resend-verification")
    public  ResponseEntity<MessageResponse> resendVerificationLink(@Valid @RequestBody EmailRequest request){
        return ResponseEntity.ok(authService.resendVerificationLink(request));
    }


    @PostMapping("/forgot-password-request")
    public ResponseEntity<MessageResponse> forgotPasswordRequest(@Valid @RequestBody  EmailRequest request){
        return ResponseEntity.ok(authService.forgotPasswordRequest(request));
    }

//    http://localhost:1111/api/v1.1/auth/verifyForgotPasswordRequest?token=6bcac1d4-4b5e-493b-8654-0a3503465aaa
    @GetMapping("/verifyForgotPasswordRequest")
    public ResponseEntity<MessageResponse> verifyForgotPasswordRequest(@RequestParam String token){
        return ResponseEntity.ok(authService.verifyForgotPasswordRequest(token));
    }

    @PostMapping("/change-forgot-password")
    public ResponseEntity<MessageResponse>  changeForgotPassword(@Valid @RequestBody TokenWithNewPasswordRequest request){
        return ResponseEntity.ok(authService.changeForgotPassword(request));
    }

    @PostMapping("/change-password-request-usingOtp")
    public ResponseEntity<MessageResponse> changePasswordRequestUsingOtp( @Valid @RequestBody EmailRequest request){
        return ResponseEntity.ok(authService.changePasswordRequestUsingOtp(request));
    }

    @PostMapping("change-password-usingOtp")
    public ResponseEntity<MessageResponse> changePasswordUsingOtp(@Valid @RequestBody ChangePasswordUsingOtpRequest request){
        return ResponseEntity.ok(authService.changePasswordUsingOtp(request));
    }

    @GetMapping("/getCurrentUser")
    public ResponseEntity<LoginResponse> getCurrentUser(Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(authService.getCurrentUser(email));
    }

}
