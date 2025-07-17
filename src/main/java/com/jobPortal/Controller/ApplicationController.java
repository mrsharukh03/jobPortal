package com.jobPortal.Controller;

import com.jobPortal.Service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController{

    private final ApplicationService applicationServics;

    public ApplicationController(ApplicationService applicationServics) {
        this.applicationServics = applicationServics;
    }


    @GetMapping("/jobs/{jobId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> getApplicantsForJob(@PathVariable Long jobId, @AuthenticationPrincipal UserDetails userDetails){
        return applicationServics.getApplicantsForJob(userDetails.getUsername(),jobId);
    }

    @GetMapping("/{applicationId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> getApplicantById(@PathVariable Long applicationId, @AuthenticationPrincipal UserDetails userDetails){
        return applicationServics.getApplicantById(userDetails.getUsername(),applicationId);
    }

    @PatchMapping("/{applicationId}/selection")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<String> updateSelectionStatus(
            @PathVariable Long applicationId,
            @RequestParam("selected") boolean selection,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return applicationServics.updateApplicationSelection(userDetails.getUsername(), applicationId, selection);
    }
}
