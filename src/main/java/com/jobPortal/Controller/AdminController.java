package com.jobPortal.Controller;

import com.jobPortal.DTO.AuthDTO.EmailDTO;
import com.jobPortal.Service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/")
public class AdminController{


    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok("Welcome ADMIN");
    }



    @PostMapping("/user/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activateUser(@RequestBody EmailDTO userIdDTO){
        boolean isActivated = adminService.activateUser(userIdDTO);
        if (isActivated) return new ResponseEntity<>("User Validate Successfully", HttpStatus.OK);
        return new ResponseEntity<>("Verification Failed",HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
