package com.jobPortal.Controller;

import com.jobPortal.Service.JobSeekerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
