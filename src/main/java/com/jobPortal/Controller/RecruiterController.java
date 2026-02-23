package com.jobPortal.Controller;

import com.jobPortal.DTO.JobRequestDTO;
import com.jobPortal.DTO.RecruiterDTO.*;
import com.jobPortal.Security.JwtUserPrincipal;
import com.jobPortal.Service.JobService;
import com.jobPortal.Service.RecruiterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        RecruiterViewDTO recruiterProfileViewDTO =  recruiterService.getProfile(principal.getUserId());

        if (recruiterProfileViewDTO == null) return new ResponseEntity<>("Profile neot found",HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(recruiterProfileViewDTO,HttpStatus.OK);
    }

    @PostMapping("/profile/update")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Boolean> updateProfile(
            @Valid @RequestBody RecruiterProfileDTO recruiterProfileDTO,
            @AuthenticationPrincipal JwtUserPrincipal principal) {

        boolean isupdated =  recruiterService.updateProfile(
                principal.getUserId(),
                recruiterProfileDTO
        );
        if (isupdated) return new ResponseEntity<>(false,HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(true,HttpStatus.OK);
    }


    @PutMapping("/applications/{applicationId}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<String> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody UpdateApplicationStatusDTO dto,
            @AuthenticationPrincipal JwtUserPrincipal principal) {

        jobService.updateApplicationStatus(principal.getUserId(), applicationId, dto);
        return new ResponseEntity<>("Application status updated successfully", HttpStatus.OK);
    }

    @PutMapping("/applications/status/bulk")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<String> bulkUpdateApplicationStatus(
            @Valid @RequestBody BulkUpdateApplicationStatusDTO dto,
            @AuthenticationPrincipal JwtUserPrincipal principal) {

        jobService.bulkUpdateApplicationStatus(principal.getUserId(), dto);
        return new ResponseEntity<>("Applications updated successfully", HttpStatus.OK);
    }


}
