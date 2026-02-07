package com.ankitsaahariya.Service;

import com.ankitsaahariya.dto.request.LoginRequest;
import com.ankitsaahariya.dto.request.SignupRequest;
import com.ankitsaahariya.dto.response.LoginResponse;
import com.ankitsaahariya.dto.response.MessageResponse;


public interface AuthService {

    MessageResponse signup(SignupRequest  request);

    MessageResponse verifyEmail(String token);

    LoginResponse login(LoginRequest loginRequest);
}
