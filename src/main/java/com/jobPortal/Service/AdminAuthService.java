package com.jobPortal.Service;

import com.jobPortal.DTO.AuthDTO.AuthResponseDTO;
import com.jobPortal.DTO.AuthDTO.LoginDTO;
import com.jobPortal.DTO.AuthDTO.ResetPasswordDTO;
import com.jobPortal.DTO.AuthDTO.TokenVerificationResult;
import com.jobPortal.Enums.Role;
import com.jobPortal.Exception.BadRequestException;
import com.jobPortal.Exception.BusinessException;
import com.jobPortal.Exception.UserNotFoundException;
import com.jobPortal.Model.Users.User;
import com.jobPortal.Repository.UserRepository;
import com.jobPortal.Security.JWTUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class AdminAuthService {

    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

    public AdminAuthService(UserRepository userRepository, EmailVerificationService emailVerificationService, PasswordEncoder passwordEncoder, JWTUtils jwtUtils) {
        this.userRepository = userRepository;
        this.emailVerificationService = emailVerificationService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    /* ================= LOGIN ================= */

    public AuthResponseDTO login(LoginDTO dto) {

        User user = findAdmin(dto.getEmail());

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            throw new BadRequestException("Invalid credentials");

        validateAdminState(user);

        return buildAuth(user);
    }

    /* ================= SEND RESET LINK ================= */

    public void sendResetLink(String email) {
        User user = findAdmin(email);
        try {
        emailVerificationService.sendRecoveryOptionsEmail(user.getEmail());
        }catch (Exception e){
            throw new BusinessException("Failed to send verification link");
        }
    }

    /* ================= RESET PASSWORD ================= */

    @Transactional
    public void resetPassword(ResetPasswordDTO dto) {

        TokenVerificationResult result =
                emailVerificationService.verifyToken(dto.getToken());

        if (!result.isSuccess())
            throw new BadRequestException(result.getMessage());

        User user = findAdmin(result.getEmail());

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setVerified(true);
        user.setActive(true);
        user.setUpdateTime(LocalDateTime.now());

        userRepository.save(user);
    }

    /* ================= DIRECT LOGIN ================= */

    @Transactional
    public AuthResponseDTO directLogin(String token) {

        TokenVerificationResult result =
                emailVerificationService.verifyToken(token);

        if (!result.isSuccess())
            throw new BadRequestException(result.getMessage());

        User user = findAdmin(result.getEmail());

        validateAdminState(user);

        return buildAuth(user);
    }

    /* ================= HELPERS ================= */

    private User findAdmin(String email) {
        User user = userRepository.findByEmail(email.toLowerCase());

        if (user == null)
            throw new UserNotFoundException("Admin not found");

        if (!user.getRole().contains(Role.ADMIN) &&
                !user.getRole().contains(Role.SUPER_ADMIN))
            throw new BadRequestException("Not an admin account");

        return user;
    }

    private void validateAdminState(User user) {
        if (!user.isVerified())
            throw new BadRequestException("Email not verified");

        if (!user.isActive())
            throw new BadRequestException("Admin account disabled");
    }

    private AuthResponseDTO buildAuth(User user) {
        return new AuthResponseDTO(
                jwtUtils.generateAccessToken(
                        user.getEmail(),
                        user.getId(),
                        new ArrayList<>(user.getRole())
                ),
                null
        );
    }
}

