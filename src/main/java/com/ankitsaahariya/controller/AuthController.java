package com.ankitsaahariya.controller;

import com.ankitsaahariya.Service.AuthService;
import com.ankitsaahariya.dto.request.SignupRequest;
import com.ankitsaahariya.dto.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signup(@RequestBody  SignupRequest signupRequest){
        return ResponseEntity.ok(authService.signup(signupRequest));
    }

}
