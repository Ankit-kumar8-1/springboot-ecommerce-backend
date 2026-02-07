package com.ankitsaahariya.controller;

import com.ankitsaahariya.Service.AuthService;
import com.ankitsaahariya.dto.request.SignupRequest;
import com.ankitsaahariya.dto.response.MessageResponse;
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

}
