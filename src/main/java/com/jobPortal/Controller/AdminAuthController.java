package com.jobPortal.Controller;

import com.jobPortal.DTO.AuthDTO.*;
import com.jobPortal.Service.AdminAuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/auth")
@Tag(name = "Admin Auth")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    /* ================= LOGIN (ADMIN / SUPER ADMIN) ================= */

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginDTO dto,
            HttpServletResponse response
    ) {
        AuthResponseDTO auth = adminAuthService.login(dto);
        setAdminCookie(response, auth.getAccessToken());

        return ResponseEntity.ok(
                Map.of("message", "Admin login successful")
        );
    }

    /* ================= SEND RESET / MAGIC LINK ================= */

    @PostMapping("/send-reset-link")
    public ResponseEntity<?> sendResetLink(
            @Valid @RequestBody EmailDTO dto
    ) {
        adminAuthService.sendResetLink(dto.getEmail());

        return ResponseEntity.ok(
                Map.of("message", "Password setup / login link sent to email")
        );
    }

    /* ================= RESET PASSWORD ================= */

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordDTO dto
    ) {
        adminAuthService.resetPassword(dto);

        return ResponseEntity.ok(
                Map.of("message", "Password set successfully. You can now login.")
        );
    }

    /* ================= DIRECT LOGIN (MAGIC LINK) ================= */

    @PostMapping("/direct-login")
    public ResponseEntity<?> directLogin(
            @Valid @RequestBody TokenDTO dto,
            HttpServletResponse response
    ) {
        AuthResponseDTO auth = adminAuthService.directLogin(dto.getToken());
        setAdminCookie(response, auth.getAccessToken());

        return ResponseEntity.ok(
                Map.of("message", "Admin login successful")
        );
    }

    /* ================= LOGOUT ================= */

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        ResponseCookie clearCookie = ResponseCookie.from("adminAccessToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/admin")
                .sameSite("Strict")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", clearCookie.toString());

        return ResponseEntity.ok(
                Map.of("message", "Admin logged out successfully")
        );
    }

    /* ================= COOKIE HELPER ================= */

    private void setAdminCookie(HttpServletResponse response, String token) {

        ResponseCookie adminCookie = ResponseCookie.from("adminAccessToken", token)
                .httpOnly(true)
                .secure(false)              // MUST be true in prod
                .path("/api/v1/admin")     // admin scope only
                .sameSite("Strict")
                .maxAge(10 * 60)            // 10 minutes
                .build();
        response.addHeader("Set-Cookie", adminCookie.toString());
    }
}


