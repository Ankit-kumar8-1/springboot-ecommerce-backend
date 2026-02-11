package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Exception.BadRequestException;
import com.ankitsaahariya.Exception.EmailNotVerifiedException;
import com.ankitsaahariya.Exception.UserNotFoundException;
import com.ankitsaahariya.Exception.VerificationTokenExpiredException;
import com.ankitsaahariya.Service.EmailService;
import com.ankitsaahariya.Service.SellerService;
import com.ankitsaahariya.dao.SellerIntentTokenRepository;
import com.ankitsaahariya.dao.SellerProfileRepository;
import com.ankitsaahariya.dao.UserRepository;
import com.ankitsaahariya.domain.SellerIntentStatus;
import com.ankitsaahariya.domain.SellerIntentTokenStatus;
import com.ankitsaahariya.domain.SellerVerificationStatus;
import com.ankitsaahariya.dto.request.SellerApplicationRequest;
import com.ankitsaahariya.dto.response.MessageResponse;
import com.ankitsaahariya.entities.SellerIntentToken;
import com.ankitsaahariya.entities.SellerProfile;
import com.ankitsaahariya.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final SellerIntentTokenRepository sellerIntentTokenRepository;
    private final EmailService emailService;
    private final SellerProfileRepository sellerProfileRepository;
    private final UserRepository userRepository;
    private final static int token_expire_minutes = 30;


    @Transactional
    @Override
    public MessageResponse requestSellerIntent(String email)  {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("User Not Found with this Email "+ email));

        if (!user.isEmailVerified()){
            throw  new EmailNotVerifiedException("Please Verified Email First , then you unlock All features !");
        }
        if(!user.isEnabled()){
            throw new EmailNotVerifiedException("your Account is suspended some Time !");
        }

        if(user.getSellerIntentStatus() == SellerIntentStatus.VERIFIED){
            throw new IllegalStateException("Seller intent already Verified !");
        }

        if(user.getSellerIntentStatus() == SellerIntentStatus.EMAIL_SENT){
            throw new IllegalStateException("Verified Link Already send on your Email , Check first !");
        }

        if (user.getSellerIntentRequestedAt() != null &&
                user.getSellerIntentRequestedAt().isAfter(LocalDateTime.now().minusMinutes(5))) {
            throw new IllegalStateException("Please wait before requesting again");
        }

        SellerIntentToken token = new SellerIntentToken();
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(token_expire_minutes));
        token.setToken(UUID.randomUUID().toString());
        sellerIntentTokenRepository.save(token);


        user.setSellerIntentStatus(SellerIntentStatus.EMAIL_SENT);
        user.setSellerIntentRequestedAt(LocalDateTime.now());
        userRepository.save(user);

        emailService.sendSellerIntentVerificationEmail(email,token.getToken(),user.getFullName());

        return new MessageResponse("Verification Email sent on your Email ");
    }

    @Override
    public MessageResponse verifySellerIntent(String token) {
        SellerIntentToken existsToken = sellerIntentTokenRepository.findByToken(token)
                .orElseThrow(()-> new IllegalArgumentException("Token Invalid"));

        if(existsToken.isVerified()){
            throw new IllegalStateException("Token already Used !");
        }

        if(existsToken.isExpired()){
            existsToken.setStatus(SellerIntentTokenStatus.EXPIRED);
            sellerIntentTokenRepository.save(existsToken);
            throw new VerificationTokenExpiredException("Token Expired");
        }

        existsToken.setStatus(SellerIntentTokenStatus.VERIFIED);
        existsToken.setUsedAt(LocalDateTime.now());

        UserEntity user = existsToken.getUser();
        user.setSellerIntentStatus(SellerIntentStatus.VERIFIED);

        sellerIntentTokenRepository.save(existsToken);
        userRepository.save(user);

        return new MessageResponse("Seller intent verified successfully. You can now apply as seller.");
    }

    @Transactional
    @Override
    public MessageResponse applyForSeller(SellerApplicationRequest request) {
        UserEntity user = getCurrentUser();

        // 1️⃣ Seller Intent Validation
        if (user.getSellerIntentStatus() != SellerIntentStatus.VERIFIED) {
            throw new BadRequestException("Seller intent not verified. Please verify from email before applying.");
        }

        // 2️⃣ Check if SellerProfile already exists
        Optional<SellerProfile> optionalProfile =
                sellerProfileRepository.findByUserId(user.getId());

        if (optionalProfile.isPresent()) {

            SellerProfile existingProfile = optionalProfile.get();

            switch (existingProfile.getVerificationStatus()) {

                case PENDING:
                    throw new com.ankitsaahariya.Exception.BadRequestException("Application already pending for review.");

                case APPROVED:
                    throw new com.ankitsaahariya.Exception.BadRequestException("You are already an approved seller.");

                case REJECTED:
                    // ✅ Allow reapply
                    updateSellerProfile(existingProfile, request);
                    existingProfile.setVerificationStatus(SellerVerificationStatus.PENDING);
                    existingProfile.setAppliedAt(LocalDateTime.now());

                    sellerProfileRepository.save(existingProfile);

                    return new MessageResponse(
                            "Application re-submitted successfully and is pending for review."
                    );
            }
        }

        // 3️⃣ GST must be unique
        if (sellerProfileRepository.existsByGstNumber(request.getGstNumber())) {
            throw new com.ankitsaahariya.Exception.BadRequestException("GST number already in use. Please provide a valid GST number.");
        }

        // 4️⃣ Create new SellerProfile
        SellerProfile profile = new SellerProfile();

        profile.setUser(user);
        profile.setVerificationStatus(SellerVerificationStatus.PENDING);
        profile.setAppliedAt(LocalDateTime.now());

        updateSellerProfile(profile, request);

        sellerProfileRepository.save(profile);

        return new MessageResponse(
                "Seller application submitted successfully. Waiting for admin approval."
        );
    }

    private void updateSellerProfile(SellerProfile profile,
                                     SellerApplicationRequest request) {

        // BUSINESS
        profile.setBusinessName(request.getBusinessName());
        profile.setBusinessType(request.getBusinessType());
        profile.setBusinessAddress(request.getBusinessAddress());
        profile.setBusinessCity(request.getBusinessCity());
        profile.setBusinessState(request.getBusinessState());
        profile.setBusinessPincode(request.getBusinessPincode());
        profile.setBusinessPhone(request.getBusinessPhone());
        profile.setBusinessEmail(request.getBusinessEmail());
        profile.setBusinessDescription(request.getBusinessDescription());

        // LEGAL
        profile.setGstNumber(request.getGstNumber());
        profile.setPanNumber(request.getPanNumber());
        profile.setAadharNumber(request.getAadharNumber());

        // BANK
        profile.setBankAccountNumber(request.getBankAccountNumber());
        profile.setBankIfscCode(request.getBankIfscCode());
        profile.setBankAccountHolderName(request.getBankAccountHolderName());
        profile.setBankName(request.getBankName());
        profile.setBankBranch(request.getBankBranch());

        profile.setVerificationStatus(SellerVerificationStatus.PENDING);
        profile.setAppliedAt(LocalDateTime.now());
    }

    private UserEntity getCurrentUser() {
        return userRepository.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).orElseThrow();
    }


}
