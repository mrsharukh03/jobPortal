package com.jobPortal.Controller;

import com.jobPortal.DTO.StudentDTO.EducationDTO;
import com.jobPortal.DTO.StudentDTO.PersonalDetailDTO;
import com.jobPortal.Service.SkillService;
import com.jobPortal.Service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

    public StudentController(StudentService studentService, SkillService skillService) {
        this.studentService = studentService;
        this.skillService = skillService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok("Welcome Student");
    }

    @PostMapping("/update/personalInfo")
    public ResponseEntity<?> updatePersonalInfo(@Valid @RequestBody PersonalDetailDTO personalDetailDTO, @AuthenticationPrincipal UserDetails userDetails){
        return studentService.updatePersonalInfo(personalDetailDTO,userDetails.getUsername());
    }

    @PostMapping("/update/skill")
    public ResponseEntity<?> updateSkill(@RequestBody String skill, @AuthenticationPrincipal UserDetails userDetails){
        return skillService.addSkillToStudent(userDetails.getUsername(),skill);
    }
    @PostMapping("/update/skills")
    public ResponseEntity<?> updateSkill(@RequestBody List<String> skills, @AuthenticationPrincipal UserDetails userDetails){
        return skillService.addMultipleSkillsToStudent(userDetails.getUsername(),skills);
    }

    @PostMapping("/update/education")
    public ResponseEntity<?> addEducation(@Valid @RequestBody EducationDTO educationDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return skillService.updateEducation(userDetails.getUsername(),educationDTO);
    }


    @GetMapping("skills")
    public ResponseEntity<?> getMySkills(@AuthenticationPrincipal UserDetails userDetails){
        List<String> skills = skillService.getUserSkills(userDetails.getUsername());
        if(skills.isEmpty()) return new ResponseEntity<>("User have no skills", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(skills,HttpStatus.OK);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails){
        return studentService.getProfile(userDetails.getUsername());
    }

    @GetMapping("/applications")
    public ResponseEntity<?> getAllApplications(@AuthenticationPrincipal UserDetails userDetails){
        return studentService.getApplications(userDetails.getUsername());
    }
}
