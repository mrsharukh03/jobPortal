package com.jobPortal.Controller;

import com.jobPortal.DTO.MultiUseDTO.ApplyJobDTO;
import com.jobPortal.DTO.StudentDTO.PersonalDetailDTO;
import com.jobPortal.Service.JobService;
import com.jobPortal.Service.SkillService;
import com.jobPortal.Service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student/")
public class StudentController {

    private final StudentService studentService;
    private final SkillService skillService;
    private final JobService jobService;

    public StudentController(StudentService studentService, SkillService skillService, JobService jobService) {
        this.studentService = studentService;
        this.skillService = skillService;
        this.jobService = jobService;
    }

    @PatchMapping("/update/personalInfo")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> updatePersonalInfo(@Valid @RequestBody PersonalDetailDTO personalDetailDTO, @AuthenticationPrincipal UserDetails userDetails){
        return studentService.updatePersonalInfo(personalDetailDTO,userDetails.getUsername());
    }

    @PatchMapping("/update/skills")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> updateSkill(@RequestBody List<String> skills, @AuthenticationPrincipal UserDetails userDetails){
        return skillService.addMultipleSkillsToStudent(userDetails.getUsername(),skills);
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> uploadStudentProfile(
            @RequestParam("linkedin") String linkedin,
            @RequestParam("github") String github,
            @RequestParam("image") MultipartFile image,
            @RequestParam("resume") MultipartFile resume,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
         return studentService.handleStudentUpload(linkedin, github, image, resume,userDetails.getUsername());
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails){
        return studentService.getProfile(userDetails.getUsername());
    }

    @GetMapping("/applications")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getAllApplications(@AuthenticationPrincipal UserDetails userDetails){
        return studentService.getApplications(userDetails.getUsername());
    }
}
