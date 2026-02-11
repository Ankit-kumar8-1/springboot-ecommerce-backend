package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Exception.EmailSendFailedException;
import com.ankitsaahariya.Service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceImp implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImp.class);
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    @Value("${app.backend.url}")
    private String backendUrl;

    @Override
    public void sendVerificationEmail(String toEmail, String token , String fullName) {
        try {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("E-Commerce | Verify Your Account");
            String verificationLink =
                    backendUrl + "/api/v1.1/auth/verify-email?token=" + token;

            String body =
                    "Hi " + fullName + ",\n\n" +
                            "Welcome to E-Commerce!\n\n" +
                            "Thank you for signing up with us.\n\n" +
                            "Please verify your email address by clicking the link below:\n\n" +
                            verificationLink + "\n\n" +
                            "This link is valid for 15 minutes.\n" +
                            "You will not be able to log in until your email is verified.\n\n" +
                            "If you did not create an account, please ignore this email.\n\n" +
                            "Thanks & regards,\n" +
                            "Team E-Commerce";
            message.setText(body);
            javaMailSender.send(message);
            logger.info("Verification email sent to {}",toEmail);
        }catch (Exception ex){
            logger.error("failed to send verification email to {}: {}",toEmail,ex.getMessage());
            throw  new EmailSendFailedException("failed to send verification email");
        }
    }

    @Override
    public void sendForgotPasswordRequest(String toEmail, String token, String fullName) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("E-Commerce | Forgot Password Request");
            String resetLink = backendUrl
                    + "/api/v1.1/auth/verifyForgotPasswordRequest?token="
                    + URLEncoder.encode(token, StandardCharsets.UTF_8);

            String emailBody=
                    "Hi,"+ fullName + ",\n\n" +
                            "We received a request to reset your E-Commerce Backend  account password.\n\n" +
                            "Please click the link below to reset your password:\n\n" +
                            resetLink + "\n\n" +
                            "This link is valid for a limited time. Please do not share this link with anyone.\n\n" +
                            "If you did not request this, please ignore this email.\n\n" +
                            "Best Regards,\n" +
                            "Team E-Commerce Backend";

            message.setText(emailBody);
            javaMailSender.send(message);

            logger.info("Password reset email sent to {}", toEmail);

        }catch (Exception ex){
            logger.error("Failed to send password reset email to {}: {}",toEmail,ex.getMessage(),ex);
            throw new RuntimeException("Failed to send password reset email");
        }
    }


    @Override
    public void SendChangePasswordRequestWithOpt(String toEmail, String otp, String fullName) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("E-Commerce | Change Account Password Request");

            String emailBody =
                    "Hi " + fullName + ",\n\n" +
                            "We received a request to change the password for your E-Commerce Backend account.\n\n" +
                            "Please use the following One-Time Password (OTP) to proceed with changing your password:\n\n" +
                            otp + "\n\n" +
                            "This OTP is valid for a limited time. Please do not share it with anyone.\n\n" +
                            "If you did not request a password change, please ignore this email.\n\n" +
                            "Best regards,\n" +
                            "Team E-Commerce Backend";

            message.setText(emailBody);
            javaMailSender.send(message);

            logger.info("Password reset email sent to {}", toEmail);

        }catch (Exception ex){
            logger.error("Failed to send password reset email to {}: {}",toEmail,ex.getMessage(),ex);
            throw new RuntimeException("Failed to send password reset email");
        }

    }
    //        Email for apply become seller , this Email is before filling form
    @Override
    public void sendSellerIntentVerificationEmail(String toEmail, String token, String fullName) {

        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("E-Commerce | Seller Intent Verification Email");

            String resetLink = backendUrl
                    + "/api/v1.1/seller/verify-Seller-Intent?token="
                    + URLEncoder.encode(token, StandardCharsets.UTF_8);

            String emailBody = "Hi " + fullName + ",\n\n" +
                    "Thank you for applying to become a seller on our platform!\n\n" +
                    "We're excited to have you join our seller community.\n\n" +
                    "To proceed with your application, please verify your email address by clicking the link below:\n\n" +
                    resetLink + "\n\n" +
                    "⏰ IMPORTANT: This verification link will expire in 30 minutes.\n\n" +
                    "If you didn't request to become a seller, please ignore this email.\n\n" +
                    "Need help? Contact our support team at ecommerce@yourstore.com\n\n" +
                    "Best regards,\n" +
                    "Team E-Commerce Platform";

            message.setText(emailBody);
            javaMailSender.send(message);

            logger.info("Verify seller intent email sent to {}", toEmail);

        }catch (Exception ex){
            logger.error("Failed to send password reset email to {}: {}",toEmail,ex.getMessage(),ex);
            throw new RuntimeException("Failed to send password reset email");
        }
    }
}
