package com.jobPortal.Controller;

import com.jobPortal.DTO.JobListDTO;
import com.jobPortal.DTO.JobRequestDTO;
import com.jobPortal.DTO.JobSearchFilterDTO;
import com.jobPortal.Service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
public class PublicController {

    private final JobService jobService;

    public PublicController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/popular-jobs")
    public ResponseEntity<?> getPopularJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(jobService.getPopularJobs(page, size));
    }

    @GetMapping("/jobs/{category}")
    public ResponseEntity<?> getJobsByCategory(@PathVariable String category){
        List<JobListDTO> jobRequestDTOS = jobService.findJobsByCategory(category);
        return new ResponseEntity<>(jobRequestDTOS,HttpStatus.OK);
    }

    @GetMapping("/job/{id}")
    public ResponseEntity<?> getJobsById(@PathVariable Long id){
        JobRequestDTO jobRequestDTO = jobService.getJobById(id);
        if (jobRequestDTO == null) return new ResponseEntity<>("No jobs found ",HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(jobRequestDTO,HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchJobs(@RequestBody JobSearchFilterDTO filter){
        return ResponseEntity.ok(jobService.searchJobs(filter));
    }


}
