package com.jobPortal.Controller;


import com.jobPortal.Enums.Role;
import com.jobPortal.Security.JwtUserPrincipal;
import com.jobPortal.Service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /* ================= ROLE ASSIGN ================= */

    @PostMapping("/assign-role")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> assignRole(
            @RequestParam("role") Role role,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        userService.createUserType(principal.getEmail(), role);
        return ResponseEntity.ok(
                Map.of("message", "Role assigned successfully")
        );
    }

    /* ================= CHECK PROFILE STATUS (NEW) ================= */
    @GetMapping("/profileStatusAndRole")
    public ResponseEntity<?> getProfileStatusAndRole(@AuthenticationPrincipal JwtUserPrincipal principal) {
        // Yeh logic Service me likhna behtar hai, main yahan call kar raha hu
        return userService.getUserProfileStatus(principal.getUserId());
    }
}
