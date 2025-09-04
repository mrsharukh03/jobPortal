package com.jobPortal.Service;

import com.jobPortal.DTO.AuthDTO.*;
import com.jobPortal.Enums.Role;
import com.jobPortal.Model.Users.Recruiter;
import com.jobPortal.Model.Users.Seeker;
import com.jobPortal.Model.Users.User;
import com.jobPortal.Repositorie.AdminRepository;
import com.jobPortal.Repositorie.RecruiterRepository;
import com.jobPortal.Repositorie.SeekerRepository;
import com.jobPortal.Repositorie.UserRepository;
import com.jobPortal.Security.JWTUtils;
import com.jobPortal.Util.AuthHelper;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class UserService {

    private final UserRepository userRepository;
    private final SeekerRepository seekerRepository;
    private final RecruiterRepository recruiterRepository;
    private final AdminRepository adminRepository;
    private final EmailVerificationService emailVerificationService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

    public UserService(UserRepository userRepository, SeekerRepository seekerRepository, RecruiterRepository recruiterRepository, AdminRepository adminRepository, EmailVerificationService emailVerificationService, ModelMapper modelMapper, PasswordEncoder passwordEncoder, JWTUtils jwtUtils) {
        this.userRepository = userRepository;
        this.seekerRepository = seekerRepository;
        this.recruiterRepository = recruiterRepository;
        this.adminRepository = adminRepository;
        this.emailVerificationService = emailVerificationService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public ResponseEntity<?> signup(SignupDTO signupRequest) {
        try {
            String email = signupRequest.getEmail().toLowerCase();

            if (userRepository.existsByEmail(email)) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("message", "Oops! Looks like you’re already registered. Login instead?"));
            }

            User user = new User();
            user.setFullName(signupRequest.getFullName());
            user.setEmail(signupRequest.getEmail());
            user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
            user.setActive(false);
            user.setVerified(false);
            user.setCreatedTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            user.setRole(Collections.singletonList(Role.USER));
            emailVerificationService.sendEmailVerificationLink(user.getEmail());
            userRepository.save(user);
            log.info("User registered: {}, verification email sent.", email);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("message", "User created. A verification link has been sent to your email."));
        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong. Please try again later."));
        }
    }

    public AuthResponseDTO login(LoginDTO loginDTO) throws RuntimeException{

        String email = loginDTO.getEmail().toLowerCase();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        String storedPassword = user.getPassword();

        if (!AuthHelper.isBCryptEncoded(storedPassword)) {
            throw new RuntimeException("Please reset your password");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), storedPassword)) {
            throw new RuntimeException("Invalid Password");
        }

        String accessToken = jwtUtils.generateToken(user.getEmail(), user.getRole());
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail(), user.getRole());

        return new AuthResponseDTO(accessToken, refreshToken);
    }

    public ResponseEntity<?> forgetPassword(String email) {
        try {
            email = email.toLowerCase();
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("message", "Email not found"));
            }
            emailVerificationService.sendRecoveryOptionsEmail(email);
            log.info("Recovery email sent to {}", email);
            return ResponseEntity.ok(Map.of("message", "Recovery email sent. Please check your inbox."));
        } catch (Exception e) {
            log.error("Error sending recovery email: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong. Please try again later."));
        }
    }

    public ResponseEntity<?> resetPassword(ResetPasswordDTO passwordDTO) {
        try {
            TokenVerificationResult result = emailVerificationService.verifyToken(passwordDTO.getToken());
            if (!result.isSuccess()) return new ResponseEntity<>(Map.of("message", result.getMessage()),HttpStatus.BAD_REQUEST);
            String email = result.getEmail();
            User user = userRepository.findByEmail(email);
            user.setPassword(passwordEncoder.encode(passwordDTO.getPassword()));
            userRepository.save(user);
            return new ResponseEntity<>(Map.of("message", "Password change successfully"),HttpStatus.CREATED);
        }catch (Exception e){
            log.error("Error Resting user password {}",e.getMessage());
            return new ResponseEntity<>(Map.of("message", "Something went wrong. Please try again later."),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean validateAccessToken(String token) {
        try {
            String email = jwtUtils.extractEmail(token);
            return jwtUtils.validateToken(token,email);
        } catch (Exception e) {
            log.warn("Invalid access token: {}", e.getMessage());
            return false;
        }
    }

    public String generateAccessTokenFromRefresh(String refreshToken) throws RuntimeException{
        try {
            String email = jwtUtils.extractEmail(refreshToken);

            // Check if refresh token is valid & not expired
            if (!jwtUtils.validateToken(refreshToken, email)) {
                throw new RuntimeException("Invalid or expired refresh token");
            }

            // Extract roles from refresh token
            List<String> rolesAsString = jwtUtils.extractRole(refreshToken);

            // Convert String roles to your Role enum list
            List<Role> roles = rolesAsString.stream()
                    .map(Role::valueOf)
                    .collect(Collectors.toList());

            // Generate new access token using email and roles
            return jwtUtils.generateToken(email, roles);

        } catch (Exception e) {
            log.error("Failed to generate access token from refresh token: {}", e.getMessage());
            throw new RuntimeException("Could not generate access token");
        }
    }





    public ResponseEntity<?> verifyEmail(String token) {
        try {
            TokenVerificationResult result = emailVerificationService.verifyToken(token);
            if (result.isSuccess()) {
                String email = result.getEmail();
                User user = userRepository.findByEmail(email);
                user.setVerified(true);
                userRepository.save(user);

                log.info("Email verified: {}", email);
                return ResponseEntity.ok(Map.of("message", result.getMessage()));
            }

            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", result.getMessage()));
        } catch (Exception e) {
            log.error("Error verifying email token {}: {}", token, e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An error occurred while verifying the email."));
        }
    }

    public ResponseEntity<?> resendVerificationLink(String email) {
        try {
            email = email.toLowerCase();
            User user = userRepository.findByEmail(email);

            if (user == null) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("message", "Email not found"));
            }

            if (user.isVerified()) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("message", "Email already verified"));
            }

            emailVerificationService.sendEmailVerificationLink(email);
            log.info("Verification link resent to {}", email);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("message", "Verification link sent. Please check your email."));
        } catch (Exception e) {
            log.error("Error resending verification link: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong. Please try again later."));
        }
    }

    public AuthResponseDTO directLogin(String token) throws RuntimeException{
        try{
            TokenVerificationResult result = emailVerificationService.verifyToken(token);
            if (!result.isSuccess()) {
                throw new RuntimeException(result.getMessage());
            }
            String email = result.getEmail();
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new RuntimeException("User not found");
            }

            String accessToken = jwtUtils.generateToken(user.getEmail(), user.getRole());
            String refreshToken = jwtUtils.generateRefreshToken(user.getEmail(), user.getRole());

            return new AuthResponseDTO(accessToken, refreshToken);
        }catch(RuntimeException e){
            throw new RuntimeException(e.getMessage());
        }catch (Exception e){
            log.error("Error generating token: {}",e.getMessage());
            throw new RuntimeException("User not found");
        }

    }


    public AuthResponseDTO processOAuthPostLogin(String fullName, String email) {
        email = email.toLowerCase();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName(fullName);
            newUser.setVerified(true); // Google email is already verified
            newUser.setActive(true);
            newUser.setCreatedTime(LocalDateTime.now());
            newUser.setUpdateTime(LocalDateTime.now());
            newUser.setRole(Collections.singletonList(Role.USER));

            // Random encoded password, since login is via Google only
            newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

            userRepository.save(newUser);
            log.info("New Google OAuth user created: {}", email);
        }
        String accessToken = jwtUtils.generateToken(user.getEmail(), user.getRole());
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail(), user.getRole());
        return new AuthResponseDTO(accessToken, refreshToken);
    }


    @Transactional
    public ResponseEntity<?> createUserType(String role, UserDetails userDetails) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername());
            if (user == null) return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
            if (role.equals(Role.ADMIN.toString()) || role.equals(Role.SUPER_ADMIN.toString())){
                return new ResponseEntity<>("Invalid Operation",HttpStatus.BAD_REQUEST);
            }
            if(role.equals(Role.SEEKER.toString()) && !seekerRepository.existsByUser(user)){
                user.getRole().add(Role.SEEKER);
                Seeker seeker = new Seeker();
                seeker.setUser(user);
                seeker.setCreatedTime(LocalDateTime.now());
                seeker.setUpdateTime(LocalDateTime.now());
                userRepository.save(user);
                seekerRepository.save(seeker);
            } else if (role.equals(Role.RECRUITER.toString()) && !recruiterRepository.existsByUser(user)) {
                user.getRole().add(Role.RECRUITER);
                Recruiter recruiter = new Recruiter();
                recruiter.setUser(user);
                recruiter.setCreatedTime(LocalDateTime.now());
                recruiter.setUpdateTime(LocalDateTime.now());
                userRepository.save(user);
                recruiterRepository.save(recruiter);
            }else{
                return new ResponseEntity<>("Invalid User Type",HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
        log.error("Error creating user profile: {}",e.getMessage());
        return new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Profile created successfully",HttpStatus.CREATED);
    }
}
