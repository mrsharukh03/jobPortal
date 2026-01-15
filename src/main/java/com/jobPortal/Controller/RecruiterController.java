package com.jobPortal.Controller;

import com.jobPortal.DTO.RecruiterDTO.RecruiterProfileDTO;
import com.jobPortal.Security.JwtUserPrincipal;
import com.jobPortal.Service.JobService;
import com.jobPortal.Service.RecruiterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/recruiter")
@Tag(name = "Recruiter")
public class RecruiterController {

    private final RecruiterService recruiterService;
    private final JobService jobService;

    public RecruiterController(RecruiterService recruiterService, JobService jobService) {
        this.recruiterService = recruiterService;
        this.jobService = jobService;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        return recruiterService.getProfile(principal.getUserId());
    }

    @PostMapping("/profile/update")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody RecruiterProfileDTO recruiterProfileDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        return recruiterService.updateProfile(
                userDetails.getUsername(),
                recruiterProfileDTO
        );
    }

    @GetMapping("/job/posted")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> getPostedJobs(@AuthenticationPrincipal JwtUserPrincipal principal){
        return jobService.getJobsByRecruiterUserId(principal.getEmail());
    }


}
