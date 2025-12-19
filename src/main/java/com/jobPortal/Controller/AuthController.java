package com.jobPortal.Controller;

import com.jobPortal.DTO.AuthDTO.*;
import com.jobPortal.Service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth")
public class AuthController {

    private final UserService userServices;

    public AuthController(UserService userServices) {
        this.userServices = userServices;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Server is running");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupDTO signupRequest) {
        return userServices.signup(signupRequest);
    }




    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        try {
            AuthResponseDTO authResponse = userServices.login(loginDTO);
            ResponseCookie accessCookie = ResponseCookie.from("accessToken", authResponse.getAccessToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(15 * 60)
                    .sameSite("Lax")
                    .build();

            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("Lax")
                    .build();

            response.addHeader("Set-Cookie", accessCookie.toString());
            response.addHeader("Set-Cookie", refreshCookie.toString());

            return ResponseEntity.ok("Login successful");

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", e.getMessage()));

        }
    }


    @GetMapping("/check")
    public ResponseEntity<?> checkLoginStatus(HttpServletRequest request) {
        String accessToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }
        if (accessToken == null || !userServices.validateAccessToken(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        return ResponseEntity.ok("Authenticated");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        try{
            String refreshToken = null;

            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("refreshToken".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }

            if (refreshToken == null || !userServices.validateAccessToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }

            String newAccessToken = userServices.generateAccessTokenFromRefresh(refreshToken);
            ResponseCookie newAccessCookie = ResponseCookie.from("accessToken", newAccessToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(15 * 60)
                    .sameSite("Lax")
                    .build();

            response.addHeader("Set-Cookie", newAccessCookie.toString());

            return ResponseEntity.ok("Access token refreshed");
        }catch (Exception e){
            return new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        return ResponseEntity.ok("Logged out");
    }


    @PostMapping("/email-verify/resend")
    public ResponseEntity<?> resendEmail(@Valid @RequestBody EmailDTO emailDTO) {
        return userServices.resendVerificationLink(emailDTO.getEmail());
    }

    @PostMapping("/email-verify")
    public ResponseEntity<?> emailVerification(@Valid @RequestBody TokenDTO tokenDTO) {
        return userServices.verifyEmail(tokenDTO.getToken());
    }

    @PostMapping("/password/forget")
    public ResponseEntity<?> forgetPassword(@Valid @RequestBody EmailDTO emailDTO) {
        return userServices.forgetPassword(emailDTO.getEmail());
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO passwordDTO) {
        return userServices.resetPassword(passwordDTO);
    }

    @PostMapping("/direct-login")
    public ResponseEntity<?> directLogin(@Valid @RequestBody TokenDTO tokenDTO, HttpServletResponse response) {
        try{
            AuthResponseDTO authResponse = userServices.directLogin(tokenDTO.getToken());
            ResponseCookie accessCookie = ResponseCookie.from("accessToken", authResponse.getAccessToken())
                    .httpOnly(true)
                    .secure(false)  // HTTPS ke liye true rakha hai, dev me false kar sakte ho
                    .path("/")
                    .maxAge(15 * 60)
                    .sameSite("Lax")
                    .build();

            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("Lax")
                    .build();

            response.addHeader("Set-Cookie", accessCookie.toString());
            response.addHeader("Set-Cookie", refreshCookie.toString());

            return ResponseEntity.ok("Login successful");
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
