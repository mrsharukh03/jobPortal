package com.jobPortal.Controller;

import com.jobPortal.DTO.JobPostDTO;
import com.jobPortal.Service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/job")
public class JobController {

    private final JobService jobService;


    public JobController(JobService jobService) {
        this.jobService = jobService;
    }



    @PostMapping("/post")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> postJob(@Valid JobPostDTO jobPostDTO,@AuthenticationPrincipal UserDetails userDetails){
        return jobService.postJob(userDetails.getUsername(),jobPostDTO);
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getPosts(@AuthenticationPrincipal UserDetails userDetails){
        return jobService.getAllPosts(userDetails.getUsername());
    }


    @DeleteMapping("/post")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> deletePost(@RequestParam Long postId, @AuthenticationPrincipal UserDetails userDetails){
        return jobService.deletePost(userDetails.getUsername(),postId);
    }

    
}
