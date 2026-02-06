package com.ankitsaahariya.Service;

import org.springframework.stereotype.Service;


public interface EmailService {

    void sendVerificationEmail(String toEmail,String token,String fullName);
}
