package com.jobPortal.Controller;


import com.jobPortal.Service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User")
public class UserController{
    private final UserService userServices;


    public UserController(UserService userServices) {
        this.userServices = userServices;
    }

    @PostMapping("/userRole")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createJobSeekerProfile(@PathParam("role") String role, @AuthenticationPrincipal UserDetails userDetails){
        return userServices.createUserType(role,userDetails);
    }
}
