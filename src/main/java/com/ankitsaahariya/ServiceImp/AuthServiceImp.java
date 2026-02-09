package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Exception.*;
import com.ankitsaahariya.Service.AuthService;
import com.ankitsaahariya.Service.EmailService;
import com.ankitsaahariya.dao.EmailVerificationTokenRepository;
import com.ankitsaahariya.dao.UserRepository;
import com.ankitsaahariya.domain.Role;
import com.ankitsaahariya.dto.request.*;
import com.ankitsaahariya.dto.response.LoginResponse;
import com.ankitsaahariya.dto.response.MessageResponse;
import com.ankitsaahariya.entities.EmailVerificationToken;
import com.ankitsaahariya.entities.UserEntity;
import com.ankitsaahariya.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private  final PasswordEncoder passwordEncoder;
    private final  JwtUtil jwtUtil;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final AuthenticationManager authenticationManager;

    @Transactional
    @Override
    public MessageResponse signup(SignupRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw  new EmailAlreadyExistsException("Email Already Exists !");
        }


        UserEntity newUser = new UserEntity();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setFullName(request.getFullName());
        newUser.setRole(Role.ROLE_CUSTOMER);
        newUser.setMobilNumber(request.getMobileNumber());

        newUser = userRepository.save(newUser);

        String token = UUID.randomUUID().toString();
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setToken(token);
        emailVerificationToken.setUser(newUser);
        emailVerificationToken.setExpiryTime(LocalDateTime.now().plusSeconds(3));
        emailVerificationTokenRepository.save(emailVerificationToken);

        emailService.sendVerificationEmail(request.getEmail(),token,request.getFullName());
        return new MessageResponse("Registration successful ! Please check your email to verify your account");
    }

    @Override
    @Transactional
    public MessageResponse verifyEmail(String token) {

        EmailVerificationToken verifyToken =
                emailVerificationTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new InvalidVerificationTokenException("Invalid verification token"));

        if (verifyToken.isUsed()) {
            throw new InvalidVerificationTokenException("Verification token already used");
        }

        if (verifyToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new VerificationTokenExpiredException("Verification token expired");
        }

        UserEntity user = verifyToken.getUser();
        if (user == null) {
            throw new UserNotFoundException("User associated with token not found");
        }

        if (user.isEmailVerified()) {
            throw new UserAlreadyVerifiedException("Email already verified");
        }

        user.setEmailVerified(true);
        verifyToken.setUsed(true);

        userRepository.save(user);
        emailVerificationTokenRepository.save(verifyToken);

        return new MessageResponse("Email verified successfully. You can login now.");
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new
                    UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
        }catch (BadCredentialsException ex){
            throw new InvalidCredentialsException("Invalid Email or Password");
        }

        UserEntity user=  userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new UsernameNotFoundException("User not found with Email "+ request.getEmail()));

        if(!user.isEmailVerified()){
            throw  new EmailNotVerifiedException("Please verity your account , check your Email");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token  = jwtUtil.generateToken(request.getEmail(), request.getPassword());

        return new LoginResponse(token,user.getEmail(),user.getFullName(),user.getRole());
    }

    @Transactional
    @Override
    public MessageResponse resendVerificationLink(EmailRequest request) {

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new UserNotFoundException("User Not Found Please Signup First"));

        if(user.isEmailVerified()){
            throw new UserAlreadyVerifiedException("User Email  Already  verified , go to login pages");
        }

        EmailVerificationToken newToken = new EmailVerificationToken();
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setExpiryTime(LocalDateTime.now().plusMinutes(15));
        newToken.setUser(user);

        emailVerificationTokenRepository.save(newToken);
        emailService.sendVerificationEmail(
                user.getEmail(),
                newToken.getToken(),
                user.getFullName()
        );

        return new MessageResponse(
                "Please check your email, verification link has been sent");
    }

    @Override
    public MessageResponse
    forgotPasswordRequest(EmailRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new UserNotFoundException("Email is not found , please signup first"));

        if(!user.isEmailVerified()){
            throw new EmailNotVerifiedException("Please verified your Email first !");
        }

        if(user.getPasswordRestToken()!= null && user.getPasswordRestTokenExpire().isAfter(Instant.now())){
            throw new VerificationTokenStillValidException("Token is Already Valid ,Please check your Email!");
        }

        String token = UUID.randomUUID().toString();
        user.setPasswordRestToken(token);
        user.setPasswordRestTokenExpire(Instant.now().plusSeconds(600));
        user.setPasswordResetVerified(false);

        userRepository.save(user);
        emailService.sendForgotPasswordRequest(request.getEmail(),token,user.getFullName());

        return new MessageResponse("Please check your Email for  forgot password request verification ");
    }

    @Override
    public MessageResponse verifyForgotPasswordRequest(String token) {
        UserEntity user = userRepository.findByPasswordRestToken(token)
                .orElseThrow(()-> new ResourceNotFoundException("Invalid Token"));

        if(user.getPasswordResetVerified()){
            throw  new forgotPasswordRequestAlreadyAccepted("Forgot password request is Already Accepted , go to ResetPassword Api endpoint");
        }

        if(user.getPasswordRestTokenExpire() == null || Instant.now().isAfter(user.getPasswordRestTokenExpire())){
            throw new VerificationTokenExpiredException("Token Expire , Please Request again");
        }

        user.setPasswordResetVerified(true);
        userRepository.save(user);

        return new MessageResponse("Your forgot password request Accepted , you can change password now");
    }

    @Override
    public MessageResponse changeForgotPassword(TokenWithNewPasswordRequest request) {
        UserEntity user = userRepository.findByPasswordRestToken(request.getToken())
                .orElseThrow(()-> new ResourceNotFoundException("Invalid Token"));

        if(!user.getPasswordResetVerified()){
            throw new PasswordResetNotVerified("please check your email , first verified your forgot password request");
        }

        if(user.getPasswordRestTokenExpire().isBefore(Instant.now())){
            throw new VerificationTokenExpiredException("Token is Expired , Please again hit forgot password Request !");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordRestToken(null);
        user.setPasswordRestTokenExpire(null);
        user.setPasswordResetVerified(false);

        userRepository.save(user);

        return new MessageResponse("Password reset Successfully ");
    }

    @Override
    public MessageResponse changePasswordRequestUsingOtp(EmailRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new UserNotFoundException("User Email not found "));

        if(!user.isEmailVerified()){
            throw new EmailNotVerifiedException("Please verified your account first , then you perform another action !");
        }

        if (user.getOtp() != null && user.getOtpExpire().isAfter(Instant.now())){
            throw new VerificationTokenStillValidException("Otp still valid , Please check your Email ");
        }

        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        user.setOtp(otp);
        user.setOtpExpire(Instant.now().plusSeconds(600));

        userRepository.save(user);
        emailService.SendChangePasswordRequestWithOpt(request.getEmail(),otp,user.getFullName());

        return new MessageResponse("Check your Email , Otp send on your Email" + request.getEmail());
    }

    @Override
    public MessageResponse changePasswordUsingOtp(ChangePasswordUsingOtpRequest request) {
        UserEntity user  = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new UserNotFoundException("Sorry ! this Email is Invalid, Please Enter right Email"));

        if(user.getOtp() == null){
            throw new ResourceNotFoundException("Please First Send change Password Request , then change your password");
        }


        if (user.getOtpExpire().isBefore(Instant.now())){
            throw new VerificationTokenExpiredException("Otp Expire Please , Please Resend change Password request!");
        }

        if(!user.getOtp().equals(request.getOtp())){
            throw new WrongOtpException("Otp is Wrong,Please send right Otp");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setOtpExpire(null);
        user.setOtp(null);
        userRepository.save(user);

        return new MessageResponse("Password Change Successfully ! , go And login with new password");
    }

    @Override
    public LoginResponse getCurrentUser(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found Exception"));
        return new LoginResponse(null,user.getEmail(),user.getFullName(),user.getRole());

    }
}
