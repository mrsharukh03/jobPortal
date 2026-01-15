package com.jobPortal.Controller;


import com.jobPortal.Security.JwtUserPrincipal;
import com.jobPortal.Service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User")
public class UserController{
    private final UserService userServices;


    public UserController(UserService userServices) {
        this.userServices = userServices;
    }

    @PostMapping("/assign-role")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createJobSeekerProfile(@RequestParam("role") String role, @AuthenticationPrincipal JwtUserPrincipal principal){
        return userServices.createUserType(principal.getEmail(), role);
    }

    @GetMapping("/profileStatusAndRole")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserRoleAndProfileStatus(@AuthenticationPrincipal JwtUserPrincipal principal) {
        return userServices.getUserRoleAndProfileStatus(principal.getEmail());
    }

    @GetMapping("/alerts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getAlert(@AuthenticationPrincipal JwtUserPrincipal principal){
        return userServices.getAllAlerts(principal.getEmail());
    }

    @GetMapping("/profileUrl")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getProfileImage(@AuthenticationPrincipal JwtUserPrincipal principal){
        return userServices.getUserProfileImage(principal.getEmail());
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal JwtUserPrincipal principal){
        return userServices.getUserProfile(principal.getEmail());
    }
}
