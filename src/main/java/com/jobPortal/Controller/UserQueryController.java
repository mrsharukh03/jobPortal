package com.jobPortal.Controller;

import com.jobPortal.Security.JwtUserPrincipal;
import com.jobPortal.Service.UserQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User")
public class UserQueryController {

    private final UserQueryService queryService;

    public UserQueryController(UserQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/profileStatusAndRole")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> roleStatus(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                queryService.getUserRoleAndProfileStatus(principal.getEmail())
        );
    }

    @GetMapping("/alerts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> alerts(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                queryService.getAlerts(principal.getEmail())
        );
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> profile(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                queryService.getUserProfile(principal.getEmail())
        );
    }

    @GetMapping("/profileUrl")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> profileImage(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                Map.of("profileUrl",
                        queryService.getProfileImage(principal.getEmail()))
        );
    }
}
