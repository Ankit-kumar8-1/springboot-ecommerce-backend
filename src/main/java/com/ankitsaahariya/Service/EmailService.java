package com.ankitsaahariya.Service;

import org.springframework.stereotype.Service;


public interface EmailService {

    void sendVerificationEmail(String toEmail,String token,String fullName);

    void sendForgotPasswordRequest(String toEmail,String token,String fullName);

    void SendChangePasswordRequestWithOpt(String toEmail,String otp , String fullName);

    void sendSellerIntentVerificationEmail(String toEmail,String token,String fullName);

    void sendSellerApprovalEmail(String email, String businessName,String fullName);

    void sendSellerRejectionEmail(String email, String reason,String fullName);
}
