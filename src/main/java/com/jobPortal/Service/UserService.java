package com.jobPortal.Service;

import com.jobPortal.DTO.AuthDTO.*;
import com.jobPortal.Enums.Role;
import com.jobPortal.Exception.BadRequestException;
import com.jobPortal.Exception.BusinessException;
import com.jobPortal.Exception.UserNotFoundException;
import com.jobPortal.Model.Users.Recruiter;
import com.jobPortal.Model.Users.Seeker;
import com.jobPortal.Model.Users.User;
import com.jobPortal.Repository.RecruiterRepository;
import com.jobPortal.Repository.SeekerRepository;
import com.jobPortal.Repository.UserRepository;
import com.jobPortal.Security.JWTUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Log4j2
public class UserService {

    private final UserRepository userRepository;
    private final SeekerRepository seekerRepository;
    private final RecruiterRepository recruiterRepository;
    private final EmailVerificationService emailVerificationService;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

    public UserService(
            UserRepository userRepository,
            SeekerRepository seekerRepository,
            RecruiterRepository recruiterRepository,
            EmailVerificationService emailVerificationService,
            PasswordEncoder passwordEncoder,
            JWTUtils jwtUtils
    ) {
        this.userRepository = userRepository;
        this.seekerRepository = seekerRepository;
        this.recruiterRepository = recruiterRepository;
        this.emailVerificationService = emailVerificationService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    /* ================= SIGNUP ================= */

    @Transactional
    public void signup(SignupDTO dto) {
        String email = dto.getEmail().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setFullName(dto.getFullName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setVerified(false);
        user.setActive(false);
        user.setRole(new ArrayList<>(List.of(Role.USER)));
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userRepository.save(user);

        // Email sending OUTSIDE DB critical logic
        try {
            emailVerificationService.sendEmailVerificationLink(email);
        } catch (Exception ex) {
            log.error("Email verification failed for {}", email, ex);
            throw new BusinessException("Failed to send verification email");
        }
    }

    /* ================= LOGIN ================= */

    public AuthResponseDTO login(LoginDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail().toLowerCase());

        if (user == null)
            throw new UserNotFoundException("User not found");

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            throw new BadRequestException("Invalid credentials");

        if (!user.isVerified())
            throw new BadRequestException("Email not verified");

        if (!user.isActive())
            throw new BadRequestException("Account is inactive");

        boolean isAdmin = user.getRole().contains(Role.ADMIN)
                || user.getRole().contains(Role.SUPER_ADMIN);

        if (isAdmin) {
            throw new BadRequestException("Login as Admin is not Allowed here !!");
        }

        return buildAuthResponse(user);
    }

    /* ================= PASSWORD ================= */

    public void sendPasswordRecovery(String email) {
        User user = userRepository.findByEmail(email.toLowerCase());

        if (user == null)
            throw new UserNotFoundException("User not found");

        try {
            emailVerificationService.sendRecoveryOptionsEmail(user.getEmail());
        } catch (Exception ex) {
            log.error("Password recovery email failed", ex);
            throw new BusinessException("Failed to send recovery email");
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordDTO dto) {
        TokenVerificationResult result =
                emailVerificationService.verifyToken(dto.getToken());

        if (!result.isSuccess())
            throw new BadRequestException(result.getMessage());

        User user = userRepository.findByEmail(result.getEmail());
        if (user == null)
            throw new UserNotFoundException("User not found");

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUpdateTime(LocalDateTime.now());

        userRepository.save(user);
    }

    /* ================= EMAIL ================= */

    @Transactional
    public void verifyEmail(String token) {
        TokenVerificationResult result =
                emailVerificationService.verifyToken(token);

        if (!result.isSuccess())
            throw new BadRequestException(result.getMessage());

        User user = userRepository.findByEmail(result.getEmail());
        if (user == null)
            throw new UserNotFoundException("User not found");

        user.setVerified(true);
        user.setActive(true);
        user.setUpdateTime(LocalDateTime.now());

        userRepository.save(user);
    }

    /* ================= OAUTH ================= */

    @Transactional
    public AuthResponseDTO processOAuthPostLogin(String fullName, String email) {
        email = email.toLowerCase();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setFullName(fullName);
            user.setVerified(true);
            user.setActive(true);
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            user.setRole(new ArrayList<>(List.of(Role.USER)));
            user.setCreatedTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());

            userRepository.save(user);
        } else {
            user.setVerified(true);
            user.setActive(true);
            user.setUpdateTime(LocalDateTime.now());
            userRepository.save(user);
        }

        return buildAuthResponse(user);
    }

    /* ================= ROLE ================= */

    @Transactional
    public void createUserType(String email, Role role) {
        User user = userRepository.findByEmail(email.toLowerCase());

        if (user == null)
            throw new UserNotFoundException("User not found");

        if (role == Role.ADMIN || role == Role.SUPER_ADMIN)
            throw new BadRequestException("Invalid role assignment");

        if (user.getRole().contains(role))
            throw new BusinessException("Role already assigned");

        if (role == Role.SEEKER) {
            if (!seekerRepository.existsByUser(user)) {
                Seeker seeker = new Seeker();
                seeker.setUser(user);
                seekerRepository.save(seeker);
            }
            user.getRole().add(Role.SEEKER);

        } else if (role == Role.RECRUITER) {
            if (!recruiterRepository.existsByUser(user)) {
                Recruiter recruiter = new Recruiter();
                recruiter.setUser(user);
                recruiterRepository.save(recruiter);
            }
            user.getRole().add(Role.RECRUITER);
        }

        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
    }

    // 🔥 NEW METHOD: Check Profile Status (Handles Multiple Roles)
    public ResponseEntity<?> getUserProfileStatus(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        Map<String, Object> response = new HashMap<>();

        var userRoles = user.getRole();

        boolean isComplete = false;
        String finalRole = "USER"; // Default value

        // 1️⃣ Priority Check: Agar RECRUITER hai
        if (userRoles.contains(Role.RECRUITER)) {
            finalRole = "RECRUITER";
            Optional<Recruiter> recruiter = recruiterRepository.findById(userId);
            if (recruiter.isPresent() && recruiter.get().isProfileComplete()) {
                isComplete = true;
            }
        }
        // 2️⃣ Priority Check: Agar SEEKER hai
        else if (userRoles.contains(Role.SEEKER)) {
            finalRole = "SEEKER";
            Optional<Seeker> seeker = seekerRepository.findById(userId);
            if (seeker.isPresent() && seeker.get().isProfileComplete()) {
                isComplete = true;
            }
        }

        // Response me ab hum "List" string nahi, balki ek Saaf String bhejenge
        response.put("role", finalRole); // Example: "SEEKER" (Not "[USER, SEEKER]")
        response.put("isProfileComplete", isComplete);

        return ResponseEntity.ok(response);
    }
    /* ================= GENERATE NEW ACCESS TOKEN ================= */

    public String generateAccessTokenFromRefresh(String refreshToken) {

        if (!jwtUtils.validateRefreshToken(refreshToken)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        UUID userId = jwtUtils.extractUserId(refreshToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isActive() || !user.isVerified()) {
            throw new BadRequestException("User account is not active");
        }

        return jwtUtils.generateAccessToken(
                user.getEmail(),
                user.getId(),
                new ArrayList<>(user.getRole())
        );
    }

    @Transactional
    public boolean changePassword(String email, ChangePasswordDto dto) {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("Current password is incorrect");
        }

        if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be same as old password");
        }

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        return true;
    }


    @Transactional
    public AuthResponseDTO directLogin(String token) {

        TokenVerificationResult result =
                emailVerificationService.verifyToken(token);

        if (!result.isSuccess()) {
            throw new BadRequestException(result.getMessage());
        }

        User user = userRepository.findByEmail(result.getEmail());

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        if (!user.isActive()) {
            throw new BadRequestException("Account is inactive");
        }

        user.setVerified(true);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);

        return buildAuthResponse(user);
    }



    /* ================= HELPER ================= */

    private AuthResponseDTO buildAuthResponse(User user) {
        return new AuthResponseDTO(
                jwtUtils.generateAccessToken(
                        user.getEmail(),
                        user.getId(),
                        new ArrayList<>(user.getRole())
                ),
                jwtUtils.generateRefreshToken(
                        user.getEmail(),
                        user.getId()
                )
        );
    }
}
