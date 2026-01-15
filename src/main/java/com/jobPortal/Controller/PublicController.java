package com.jobPortal.Controller;

import com.jobPortal.Service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public")
public class PublicController {

    private final JobService jobService;

    public PublicController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/job")
    public ResponseEntity<?> getJobById(@RequestParam Long jobId){
        return jobService.getJobById(jobId);
    }

    @GetMapping("/popular-jobs")
    public ResponseEntity<?> getPopularJobs(){
        return jobService.getPopularJobs();
    }

    @GetMapping("/jobs/{category}")
    public ResponseEntity<?> getJobsByCatogry(@PathVariable String category){
        return jobService.findJobsByCategory(category);
    }
}
