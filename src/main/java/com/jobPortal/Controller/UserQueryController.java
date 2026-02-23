package com.jobPortal.Controller;

import com.jobPortal.DTO.AuthDTO.ChangePasswordDto;
import com.jobPortal.Security.JwtUserPrincipal;
import com.jobPortal.Service.UserQueryService;
import com.jobPortal.Service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User")
public class UserQueryController {

    private final UserQueryService queryService;
    private final UserService userService;


    public UserQueryController(UserQueryService queryService, UserService userService) {
        this.queryService = queryService;
        this.userService = userService;
    }


    @PostMapping("/password/change")
    public ResponseEntity<?> changePassword(
             @RequestBody ChangePasswordDto dto,@AuthenticationPrincipal JwtUserPrincipal principal
            ) {
      boolean isChanged =  userService.changePassword(principal.getEmail(), dto);
        if(isChanged){
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        }else {
            return ResponseEntity.badRequest().body(Map.of("message", "Current password is incorrect"));
        }
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
