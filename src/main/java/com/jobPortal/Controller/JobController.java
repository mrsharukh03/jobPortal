package com.jobPortal.Controller;

import com.jobPortal.DTO.JobPostDTO;
import com.jobPortal.DTO.RecruiterDTO.JobApplicationRecruiterViewDTO;
import com.jobPortal.Security.JwtUserPrincipal;
import com.jobPortal.Service.JobService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/job")
@Tag(name = "Job Management")
public class JobController {

    private final JobService jobService;


    public JobController(JobService jobService) {
        this.jobService = jobService;
    }



    @PostMapping("/")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Boolean> postJob(
            @Valid @RequestBody JobPostDTO jobPostDTO,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        boolean isPosted = jobService.postJob(principal.getEmail(), jobPostDTO);
        if(isPosted)
            return new ResponseEntity<>(true, HttpStatus.OK);
        else
            return new ResponseEntity<>(false,HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{postId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Boolean> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody JobPostDTO jobPostDTO,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        boolean updated = jobService.updatePost(principal.getEmail(), postId, jobPostDTO);
        return updated
                ? ResponseEntity.ok(true)
                : ResponseEntity.badRequest().body(false);
    }


    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Boolean> deletePost(@PathVariable Long postId, @AuthenticationPrincipal JwtUserPrincipal principal){
        boolean isDeleted = jobService.deletePost(principal.getEmail(), postId);
        if(isDeleted)
            return new ResponseEntity<>(true, HttpStatus.OK);
        else
            return new ResponseEntity<>(false,HttpStatus.BAD_REQUEST);
    }


    @GetMapping("recruiter/")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> getPostedJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {

        return ResponseEntity.ok(
                jobService.getJobsByRecruiterUserId(
                        principal.getEmail(),
                        page,
                        size
                )
        );
    }

    @GetMapping("/{jobId}/applications")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> getJobApplications(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {

        return ResponseEntity.ok(
                jobService.getApplicationsByJobId(
                        principal.getUserId(),
                        jobId,
                        page,
                        size
                )
        );
    }


    @GetMapping("/applications/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> getJobApplicationById(@PathVariable Long id, @AuthenticationPrincipal JwtUserPrincipal principal){
        JobApplicationRecruiterViewDTO applicationsDTO = jobService.getApplicationByApplicationId(principal.getUserId(),id);
        return new ResponseEntity<>(applicationsDTO,HttpStatus.OK);
    }

    
    
}
