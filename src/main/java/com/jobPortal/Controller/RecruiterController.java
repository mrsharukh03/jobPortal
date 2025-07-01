package com.jobPortal.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recruiter/")
public class RecruiterController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok("Welcome Recruiter");
    }


}
