package com.jobPortal.Controller;

import com.jobPortal.DTO.JobSeekerDTO.PersonalDetailDTO;
import com.jobPortal.Security.JwtUserPrincipal;
import com.jobPortal.Service.JobSeekerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seeker")
@Tag(name = "Job Seeker")
public class JobSeekerController {

    private final JobSeekerService seekerService;

    public JobSeekerController(JobSeekerService seekerService) {
        this.seekerService = seekerService;
    }

    @GetMapping("/test")
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<?> test(){
        return new ResponseEntity<>("only for job Seekers", HttpStatus.OK);
    }

    @PatchMapping("/update/personal-details")
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<?> updatePersonalDetails(@RequestBody PersonalDetailDTO personalDetailDTO,@AuthenticationPrincipal JwtUserPrincipal principal){
        // add profile details in jwt token
        return seekerService.updatePersonalDetails(personalDetailDTO, principal.getEmail());
    }
}
