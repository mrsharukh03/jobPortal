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

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /* ================= SIGNUP ================= */

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupDTO dto) {
        userService.signup(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully. Please verify email."));
    }

    /* ================= LOGIN ================= */

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginDTO dto,
            HttpServletResponse response
    ) {
        AuthResponseDTO auth = userService.login(dto);
        setAuthCookies(response, auth);
        return ResponseEntity.ok(Map.of("message", "Login successful"));
    }

    /* ================= REFRESH TOKEN ================= */

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh token missing"));
        }

        String newAccessToken = userService.generateAccessTokenFromRefresh(refreshToken);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(15 * 60)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());

        return ResponseEntity.ok(Map.of("message", "Access token refreshed"));
    }

    /* ================= LOGOUT ================= */

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        ResponseCookie clearAccess = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        ResponseCookie clearRefresh = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", clearAccess.toString());
        response.addHeader("Set-Cookie", clearRefresh.toString());

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    /* ================= EMAIL ================= */

    @PostMapping("/email/verify")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody TokenDTO dto) {
        userService.verifyEmail(dto.getToken());
        return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
    }

    /* ================= PASSWORD ================= */

    @PostMapping("/password/forget")
    public ResponseEntity<?> forgetPassword(@Valid @RequestBody EmailDTO dto) {
        userService.sendPasswordRecovery(dto.getEmail());
        return ResponseEntity.ok(Map.of("message", "Recovery email sent"));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        userService.resetPassword(dto);
        return ResponseEntity.ok(Map.of("message", "Password reset successful"));
    }
    /* ================= DIRECT LOGIN (MAGIC LINK) ================= */

    @PostMapping("/direct-login")
    public ResponseEntity<?> directLogin(
            @Valid @RequestBody TokenDTO dto,
            HttpServletResponse response
    ) {
        AuthResponseDTO auth = userService.directLogin(dto.getToken());
        setAuthCookies(response, auth);
        return ResponseEntity.ok(Map.of("message", "Login successful"));
    }


    /* ================= COOKIE HELPER ================= */

    private void setAuthCookies(HttpServletResponse response, AuthResponseDTO auth) {

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", auth.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(15 * 60)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", auth.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }
}
