package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Exception.*;
import com.ankitsaahariya.Service.EmailService;
import com.ankitsaahariya.Service.SellerService;
import com.ankitsaahariya.Util.PaginationUtil;
import com.ankitsaahariya.dao.SellerIntentTokenRepository;
import com.ankitsaahariya.dao.SellerProfileRepository;
import com.ankitsaahariya.dao.UserRepository;
import com.ankitsaahariya.domain.Role;
import com.ankitsaahariya.domain.SellerIntentStatus;
import com.ankitsaahariya.domain.SellerIntentTokenStatus;
import com.ankitsaahariya.domain.SellerVerificationStatus;
import com.ankitsaahariya.dto.request.SellerApplicationRequest;
import com.ankitsaahariya.dto.request.SellerStatusUpdateRequest;
import com.ankitsaahariya.dto.response.MessageResponse;
import com.ankitsaahariya.dto.response.PageResponse;
import com.ankitsaahariya.dto.response.SellerApplicationDetailResponse;
import com.ankitsaahariya.dto.response.SellerProfileResponse;
import com.ankitsaahariya.entities.SellerIntentToken;
import com.ankitsaahariya.entities.SellerProfile;
import com.ankitsaahariya.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private static final Logger logger = LoggerFactory.getLogger(SellerServiceImpl.class);


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


    @Override
    public PageResponse<SellerProfileResponse> getSellerApplications(
            SellerVerificationStatus status,
            int page,
            int size
    ) {

        Pageable pageable = PaginationUtil.createPageRequest(page, size, "appliedAt");

        Page<SellerProfile> sellerPage =
                sellerProfileRepository.findAllByVerificationStatus(status, pageable);

        return PaginationUtil.toPageResponse(
                sellerPage,
                SellerProfileResponse::fromEntity
        );
    }

    @Override
    public SellerApplicationDetailResponse getSellerApplicationDetail(Long sellerProfileId) {

        SellerProfile profile = sellerProfileRepository.findById(sellerProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller application not found"));

        return mapToDetailResponse(profile);
    }

    @Override
    public MessageResponse updateSellerStatus(Long sellerId, SellerStatusUpdateRequest request) {

        SellerProfile profile =sellerProfileRepository.findByUserId(sellerId)
                .orElseThrow(()-> new ResourceNotFoundException("Seller not found with this id"+sellerId));

        if(profile.getVerificationStatus() != SellerVerificationStatus.PENDING){
            throw new BadRequestException("Only pending applications can be updated. Current status: " +
                    profile.getVerificationStatus());
        }

        UserEntity admin = getCurrentUser();
        if(admin.getRole() != Role.ROLE_ADMIN){
            throw new ForbiddenException("Only admin can update Seller status");
        }

        UserEntity user = profile.getUser();
        if (user == null) {
            throw new ResourceNotFoundException("User not found for seller profile");
        }

        SellerVerificationStatus newStatus = request.getStatus();
        switch (newStatus) {

            case APPROVED -> {
                // Update profile
                profile.setVerificationStatus(SellerVerificationStatus.APPROVED);
                profile.setVerifiedAt(LocalDateTime.now());
                profile.setVerifiedByAdmin(admin);
                profile.setAdminRemarks(request.getRemarks());
                profile.setIsActive(true);

                // Update user role
                user.setRole(Role.ROLE_SELLER);
                userRepository.save(user);

                // Send approval email
                try {
                    emailService.sendSellerApprovalEmail(
                            user.getEmail(),
                            profile.getBusinessName(),
                            user.getFullName()
                    );
                } catch (Exception e) {
                    logger.error("Failed to send approval email", e);
                    // Don't fail the transaction if email fails
                }

                // Audit log
                logger.info("Admin {} approved seller application {}", admin.getId(), sellerId);
            }

            case REJECTED -> {
                // Validate remarks
                if (request.getRemarks() == null || request.getRemarks().isBlank()) {
                    throw new BadRequestException(
                            "Rejection reason is required when rejecting application"
                    );
                }

                // Update profile
                profile.setVerificationStatus(SellerVerificationStatus.REJECTED);
                profile.setVerifiedAt(LocalDateTime.now());
                profile.setVerifiedByAdmin(admin);
                profile.setAdminRemarks(request.getRemarks());
                profile.setIsActive(false);

                // User role remains CUSTOMER - don't change

                // Send rejection email
                try {
                    emailService.sendSellerRejectionEmail(
                            user.getEmail(),
                            request.getRemarks(),
                            user.getFullName()
                    );
                } catch (Exception e) {
                    logger.error("Failed to send rejection email", e);
                }

                // Audit log
                logger.info("Admin {} rejected seller application {}", admin.getId(), sellerId);
            }

            default -> throw new BadRequestException(
                    "Invalid status transition. Only APPROVED or REJECTED allowed"
            );
        }

        sellerProfileRepository.save(profile);

        return new MessageResponse(
                "Seller application " + newStatus.toString().toLowerCase() + " successfully"
        );
    }



    private SellerApplicationDetailResponse mapToDetailResponse(SellerProfile profile) {

        return SellerApplicationDetailResponse.builder()
                .sellerProfileId(profile.getId())

                // USER
                .userId(profile.getUser().getId())
                .fullName(profile.getUser().getFullName())
                .email(profile.getUser().getEmail())

                // BUSINESS
                .businessName(profile.getBusinessName())
                .businessType(profile.getBusinessType())
                .businessAddress(profile.getBusinessAddress())
                .businessCity(profile.getBusinessCity())
                .businessState(profile.getBusinessState())
                .businessPincode(profile.getBusinessPincode())
                .businessPhone(profile.getBusinessPhone())
                .businessEmail(profile.getBusinessEmail())
                .businessDescription(profile.getBusinessDescription())

                // LEGAL
                .gstNumber(profile.getGstNumber())
                .panNumber(profile.getPanNumber())
                .aadharNumber(profile.getAadharNumber())

                // BANK
                .bankAccountNumber(profile.getBankAccountNumber())
                .bankIfscCode(profile.getBankIfscCode())
                .bankAccountHolderName(profile.getBankAccountHolderName())
                .bankName(profile.getBankName())
                .bankBranch(profile.getBankBranch())

                // STATUS
                .verificationStatus(profile.getVerificationStatus())
                .appliedAt(profile.getAppliedAt())
                .verifiedAt(profile.getVerifiedAt())
                .adminRemarks(profile.getAdminRemarks())

                .build();
    }



    private UserEntity getCurrentUser() {
        return userRepository.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).orElseThrow();
    }


}
