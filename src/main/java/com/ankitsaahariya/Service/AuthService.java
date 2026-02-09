package com.ankitsaahariya.Service;

import com.ankitsaahariya.dto.request.*;
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

    MessageResponse changeForgotPassword(@Valid TokenWithNewPasswordRequest request);

    MessageResponse changePasswordRequestUsingOtp(EmailRequest request);

    MessageResponse changePasswordUsingOtp(@Valid ChangePasswordUsingOtpRequest request);

    LoginResponse getCurrentUser(String email);
}
