package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Exception.*;
import com.ankitsaahariya.Service.AuthService;
import com.ankitsaahariya.Service.EmailService;
import com.ankitsaahariya.dao.EmailVerificationTokenRepository;
import com.ankitsaahariya.dao.UserRepository;
import com.ankitsaahariya.domain.Role;
import com.ankitsaahariya.dto.request.LoginRequest;
import com.ankitsaahariya.dto.request.ResendVerificationRequest;
import com.ankitsaahariya.dto.request.SignupRequest;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
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
    public MessageResponse resendVerificationLink(ResendVerificationRequest request) {

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
}
