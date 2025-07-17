package com.jobPortal.Controller;

import com.jobPortal.DTO.RecruiterDTO.RecruiterProfileDTO;
import com.jobPortal.Service.JobService;
import com.jobPortal.Service.RecruiterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recruiter/")
public class RecruiterController {

    private final RecruiterService recruiterService;
    private final JobService jobService;


    public RecruiterController(RecruiterService recruiterService, JobService jobService) {
        this.recruiterService = recruiterService;
        this.jobService = jobService;
    }


    @PostMapping("/update/profile")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody RecruiterProfileDTO profileDTO, @AuthenticationPrincipal UserDetails userDetails){
        return recruiterService.updateProfile(userDetails.getUsername(),profileDTO);
    }

}
