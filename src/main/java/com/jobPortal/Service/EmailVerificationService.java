package com.jobPortal.Service;

import com.jobPortal.DTO.AuthDTO.MailSendResult;
import com.jobPortal.DTO.AuthDTO.TokenVerificationResult;
import com.jobPortal.Model.EmailVerification;
import com.jobPortal.Repositorie.EmailVerificationRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailVerificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationService.class);

    private final EmailVerificationRepo verificationRepo;
    private final MailService mailService;

    @Value("${app.url}")
    private String url;

    public EmailVerificationService(EmailVerificationRepo verificationRepo, MailService mailService) {
        this.verificationRepo = verificationRepo;
        this.mailService = mailService;
    }


    public String generateVerificationToken(String email) throws Exception {
        try {
            // Clean up old token if it exists
            EmailVerification exestingData = verificationRepo.findByEmail(email);

            if (exestingData != null) {
                verificationRepo.delete(exestingData);
            }

            // Generate and save a new token
            EmailVerification verificationDTO = new EmailVerification();
            verificationDTO.setEmail(email);
            verificationDTO.setToken(UUID.randomUUID().toString());
            verificationDTO.setGenerationTime(LocalDateTime.now());
            verificationDTO.setExpirationTime(LocalDateTime.now().plusMinutes(5));
            verificationRepo.save(verificationDTO);

            return verificationDTO.getToken();
        } catch (Exception e) {
            log.error("Error generating verification link for email {}: {}", email, e.getMessage(), e);
            throw new Exception("Something went wrong");
        }
    }

    public void sendEmailVerificationLink(String email) throws Exception {
        try {
            Map<String, String> links = new HashMap<>();
            String verificationLink = url + "/verify-email/" + generateVerificationToken(email);
            links.put("Email Verification Link", verificationLink);

            MailSendResult result = mailService.sendVerificationLink(email, links);
            if (!result.isSuccess()) {
                throw new Exception(result.getMessage());
            }
        } catch (Exception e) {
            log.error("Error sending verification link: {}", e.getMessage(), e);
            throw new Exception("Something went wrong, try again later");
        }
    }


    public void sendRecoveryOptionsEmail(String email) throws Exception {
        try {
            Map<String, String> links = new HashMap<>();
            String token = generateVerificationToken(email);
            String resetPassword = url + "/reset-password/" + token;

            links.put("Reset Password", resetPassword);

            MailSendResult result = mailService.sendVerificationLink(email, links);
            if (!result.isSuccess()) {
                throw new Exception(result.getMessage());
            }
        } catch (Exception e) {
            log.error("Error sending recovery email: {}", e.getMessage(), e);
            throw new Exception("Something went wrong. Try again later.");
        }
    }

    public TokenVerificationResult verifyToken(String token) {
        try {
            Optional<EmailVerification> optionalVerification = verificationRepo.findById(token);
            if (optionalVerification.isEmpty()) {
                return new TokenVerificationResult(false, null, "Token not found or already used");
            }
            EmailVerification verification = optionalVerification.get();
            if (LocalDateTime.now().isAfter(verification.getExpirationTime())) {
                if (verificationRepo.existsById(token)) {
                    verificationRepo.deleteById(token);
                }
                return new TokenVerificationResult(false, verification.getEmail(), "Token expired");
            } else {
                if (verificationRepo.existsById(token)) {
                    verificationRepo.deleteById(token);
                }
                return new TokenVerificationResult(true, verification.getEmail(), "Token verified successfully");
            }

        } catch (RuntimeException e) {
            log.warn("Concurrent access: Token already processed: {}", token);
            return new TokenVerificationResult(false, null, "Token already used or expired.");
        } catch (Exception e) {
            log.error("Error verifying token: {}", e.getMessage(), e);
            return new TokenVerificationResult(false, null, "Internal server error");
        }
    }


}
