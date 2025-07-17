package com.jobPortal.Controller;
import com.jobPortal.DTO.JobStatusUpdateRequest;
import com.jobPortal.DTO.MultiUseDTO.ApplyJobDTO;
import com.jobPortal.DTO.RecruiterDTO.JobPostRequestDTO;
import com.jobPortal.Enums.JobStatus;
import com.jobPortal.Service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/jobs/")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> newPost(@RequestBody JobPostRequestDTO postRequestDTO, @AuthenticationPrincipal UserDetails userDetails){
        return jobService.newPost(userDetails.getUsername(),postRequestDTO);
    }

    @PostMapping("/apply")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> applyJob(@RequestBody ApplyJobDTO applyJobDTO, @AuthenticationPrincipal UserDetails userDetails){
        return jobService.applyJob(userDetails.getUsername(),applyJobDTO);
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return jobService.getAllJobs(keyword, location, page, size);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<?> getJobById(@PathVariable Long jobId) {
        return jobService.getJobById(jobId);
    }

    @PutMapping("/{jobId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> updateJob(@PathVariable Long jobId, @RequestBody JobPostRequestDTO jobPostRequestDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return jobService.updateJob(userDetails.getUsername(), jobId, jobPostRequestDTO);
    }


    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> deleteJob(@PathVariable Long jobId, @AuthenticationPrincipal UserDetails userDetails) {
        return jobService.deleteJob(userDetails.getUsername(), jobId);
    }

    @PatchMapping("/update/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> updateJobStatus( @Valid
            @RequestBody JobStatusUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if(request.getJobStatus() == JobStatus.EXPIRED || request.getJobStatus() == JobStatus.DELETED)
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        return jobService.updateJobStatus(userDetails.getUsername(), request);
    }


}
