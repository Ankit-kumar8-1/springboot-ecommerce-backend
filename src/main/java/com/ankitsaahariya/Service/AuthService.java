package com.ankitsaahariya.Service;

import com.ankitsaahariya.dto.request.LoginRequest;
import com.ankitsaahariya.dto.request.EmailRequest;
import com.ankitsaahariya.dto.request.SignupRequest;
import com.ankitsaahariya.dto.response.LoginResponse;
import com.ankitsaahariya.dto.response.MessageResponse;
import jakarta.validation.Valid;


public interface AuthService {

    MessageResponse signup(SignupRequest  request);

    MessageResponse verifyEmail(String token);

    LoginResponse login(LoginRequest loginRequest);

    MessageResponse resendVerificationLink(EmailRequest request);


    MessageResponse forgotPasswordRequest( EmailRequest request);


    MessageResponse verifyForgotPasswordRequest(String token);
}
