package com.jobPortal.Controller;

import com.jobPortal.DTO.StudentDTO.EducationDTO;
import com.jobPortal.Service.EducationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/education")
public class EducationController {

    private final EducationService educationService;

    public EducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    @PostMapping("/")
    public ResponseEntity<String> addEducation(@RequestBody EducationDTO educationDTO,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        return educationService.addEducation(userDetails.getUsername(), educationDTO);
    }

    // Get all education records for the student
    @GetMapping("/")
    public ResponseEntity<List<EducationDTO>> getAllEducation(@AuthenticationPrincipal UserDetails userDetails) {
        var educationList = educationService.getAllEducations(userDetails.getUsername());
        if (educationList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(educationList);
    }

    // Get education by degree name
    @GetMapping("/{degreeName}")
    public ResponseEntity<EducationDTO> getEducationByDegree(@PathVariable String degreeName,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        var education = educationService.getEducationByDegree(userDetails.getUsername(), degreeName);
        if (education == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(education);
    }

    // Update education (you can choose PUT or PATCH)
    @PutMapping("/{degreeName}")
    public ResponseEntity<String> updateEducation(@PathVariable String degreeName,
                                                  @RequestBody EducationDTO educationDTO,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        return educationService.updateEducationByDegree(userDetails.getUsername(), degreeName, educationDTO);
    }

    // Delete education
    @DeleteMapping("/{degreeName}")
    public ResponseEntity<String> deleteEducation(@PathVariable String degreeName,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        return educationService.deleteEducation(userDetails.getUsername(), degreeName);
    }
}
