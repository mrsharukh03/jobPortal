package com.jobPortal.Controller;

import com.jobPortal.Security.JwtUserPrincipal;
import com.jobPortal.Service.SavedJobService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/saved-jobs")
public class SavedJobController {
    private final SavedJobService savedJobService;

    public SavedJobController(SavedJobService savedJobService) {
        this.savedJobService = savedJobService;
    }


    /* ================= SAVE JOB ================= */
    @PostMapping("/save")
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<?> saveJob(@RequestParam Long jobId, @AuthenticationPrincipal JwtUserPrincipal principal){
        boolean isSaved = savedJobService.saveJob(principal.getUserId(), jobId);
        if(isSaved)
            return ResponseEntity.ok("Job saved successfully");
        else
            return ResponseEntity.badRequest().body("Failed to save job. Please try again.");
    }

    /* ================= GET SAVED JOBS ================= */
    @GetMapping("/saved")
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<?> getSavedJobs(@AuthenticationPrincipal JwtUserPrincipal principal){
        return ResponseEntity.ok(savedJobService.getSavedJobs(principal.getUserId()));
    }

    /* ================= DELETE SAVED JOB ================= */
    @DeleteMapping("/unsave")
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<?> unsaveJob(@RequestParam Long jobId, @AuthenticationPrincipal JwtUserPrincipal principal){
        boolean isUnSaved = savedJobService.unsaveJob(principal.getUserId(), jobId);
        if(isUnSaved) return ResponseEntity.ok("Job unsaved successfully");
        else return ResponseEntity.badRequest().body("Failed to unsave job. Please try again.");
    }
}
