package com.jobPortal.Controller;

import com.jobPortal.DTO.JobSeekerDTO.*;
import com.jobPortal.Security.JwtUserPrincipal;
import com.jobPortal.Service.JobSeekerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seeker")
@Tag(name = "Candidate Actions")
public class JobSeekerController {

    private final JobSeekerService seekerService;

    public JobSeekerController(JobSeekerService seekerService) {
        this.seekerService = seekerService;
    }

    @PostMapping(value = "/job/{jobId}/apply")
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<Boolean> quickJobApply(@PathVariable Long jobId,
                                                 @AuthenticationPrincipal JwtUserPrincipal principal){
        boolean isApplied = seekerService.quickJobApply(jobId, principal.getUserId());
        return new ResponseEntity<>(isApplied, HttpStatus.OK);
    }

    @GetMapping("/applications")
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<List<JobApplicationListDTO>> getApplications(@AuthenticationPrincipal JwtUserPrincipal principal){
        return ResponseEntity.ok(seekerService.getJobApplicationsByJobSeekerId(principal.getUserId()));
    }

    @GetMapping("/application/{id}")
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<JobApplicationDTO> getApplication(@PathVariable Long id,
                                                            @AuthenticationPrincipal JwtUserPrincipal principal) {
        return ResponseEntity.ok(seekerService.getApplicationById(id, principal.getUserId()));
    }

    @PatchMapping("/update/personal-details")
    public ResponseEntity<String> updatePersonalDetails(@Valid @RequestBody PersonalDetailDTO dto,
                                                        @AuthenticationPrincipal JwtUserPrincipal principal) {
        seekerService.updatePersonalDetails(dto, principal.getUserId());
        return ResponseEntity.ok("Personal details updated successfully");
    }

    @PatchMapping("/update-professional")
    public ResponseEntity<String> updateProfessional(@RequestBody ProfessionalDetailsDTO dto,
                                                     @AuthenticationPrincipal JwtUserPrincipal principal) {
        seekerService.updateProfessionalDetails(dto, principal.getUserId());
        return ResponseEntity.ok("Professional details updated successfully");
    }

    @DeleteMapping("/skill/{skillId}")
    public ResponseEntity<Boolean> removeSkill(@PathVariable Long skillId, @AuthenticationPrincipal JwtUserPrincipal principal) {
        boolean isRemoved = seekerService.removeSkill(skillId, principal.getUserId());
        if (!isRemoved) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
        return ResponseEntity.ok(true);
    }

    @PostMapping("/educations")
    public ResponseEntity<Boolean> addEducation(@Valid @RequestBody EducationDTO dto,
                                                @AuthenticationPrincipal JwtUserPrincipal principal) {
        boolean isSaved = seekerService.addEducation(dto, principal.getUserId());
        if (!isSaved) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        return new ResponseEntity<>(true, HttpStatus.CREATED);
    }

    @PutMapping("/education/{educationId}")
    public ResponseEntity<String> updateEducation(@PathVariable Long educationId,
                                                  @Valid @RequestBody EducationDTO dto,
                                                  @AuthenticationPrincipal JwtUserPrincipal principal) {
        seekerService.updateEducation(educationId, dto, principal.getUserId());
        return ResponseEntity.ok("Education updated successfully");
    }

    @DeleteMapping("/education/{educationId}")
    public ResponseEntity<String> deleteEducation(@PathVariable Long educationId,
                                                  @AuthenticationPrincipal JwtUserPrincipal principal) {
        seekerService.deleteEducation(educationId, principal.getUserId());
        return ResponseEntity.ok("Education deleted successfully");
    }

    @PostMapping("/experience")
    public ResponseEntity<Boolean> addExperience(@Valid @RequestBody ExperienceDTO dto,
                                                 @AuthenticationPrincipal JwtUserPrincipal principal) {
        boolean isSaved = seekerService.addExperience(dto, principal.getUserId());
        if (!isSaved) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        return new ResponseEntity<>(true, HttpStatus.CREATED);
    }

    @PutMapping("/experience/{experienceId}")
    public ResponseEntity<String> updateExperience(@PathVariable Long experienceId,
                                                   @Valid @RequestBody ExperienceDTO dto,
                                                   @AuthenticationPrincipal JwtUserPrincipal principal) {
        seekerService.updateExperience(experienceId, dto, principal.getUserId());
        return ResponseEntity.ok("Experience updated successfully");
    }

    @DeleteMapping("/experience/{experienceId}")
    public ResponseEntity<String> deleteExperience(@PathVariable Long experienceId,
                                                   @AuthenticationPrincipal JwtUserPrincipal principal) {
        seekerService.deleteExperience(experienceId, principal.getUserId());
        return ResponseEntity.ok("Experience deleted successfully");
    }

    @PostMapping(value = "/upload-documents", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadDocuments(
            @RequestParam(value = "resume", required = false) MultipartFile resume,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @AuthenticationPrincipal JwtUserPrincipal principal) {
        seekerService.uploadDocuments(resume, profileImage, principal.getUserId());
        return ResponseEntity.ok("Documents uploaded successfully");
    }

    @GetMapping("/current-profile")
    public ResponseEntity<SeekerFullProfileDTO> getCurrentProfile(@AuthenticationPrincipal JwtUserPrincipal principal) {
        return ResponseEntity.ok(seekerService.getFullProfile(principal.getUserId()));
    }

    @GetMapping("/personal-details")
    public ResponseEntity<PersonalDetailDTO> getPersonalDetails(@AuthenticationPrincipal JwtUserPrincipal principal) {
        return ResponseEntity.ok(seekerService.getPersonalDetails(principal.getUserId()));
    }

    @GetMapping("/professional")
    public ResponseEntity<ProfessionalDetailsDTO> getProfessionalDetails(@AuthenticationPrincipal JwtUserPrincipal principal) {
        return ResponseEntity.ok(seekerService.getProfessionalDetails(principal.getUserId()));
    }

    @GetMapping("/education")
    public ResponseEntity<List<EducationResponse>> getEducations(@AuthenticationPrincipal JwtUserPrincipal principal) {
        return ResponseEntity.ok(seekerService.getEducations(principal.getUserId()));
    }

    @GetMapping("/experience")
    public ResponseEntity<List<ExperienceResponse>> getExperiences(@AuthenticationPrincipal JwtUserPrincipal principal) {
        return ResponseEntity.ok(seekerService.getExperiences(principal.getUserId()));
    }

    @PostMapping("/certification")
    public ResponseEntity<Boolean> addCertification(
            @Valid @RequestBody CertificationDTO certificationDTO,
            @AuthenticationPrincipal JwtUserPrincipal principal) {
        boolean isSaved = seekerService.addCertification(certificationDTO, principal.getUserId());
        if (!isSaved) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        return new ResponseEntity<>(true, HttpStatus.CREATED);
    }

    @GetMapping("/certification")
    public ResponseEntity<List<CertificationResponse>> getCertifications(@AuthenticationPrincipal JwtUserPrincipal principal) {
        return ResponseEntity.ok(seekerService.getCertifications(principal.getUserId()));
    }

    @DeleteMapping("/certification/{certificationId}")
    public ResponseEntity<String> deleteCertification(
            @PathVariable Long certificationId,
            @AuthenticationPrincipal JwtUserPrincipal principal) {
        seekerService.deleteCertification(certificationId, principal.getUserId());
        return ResponseEntity.ok("Certification deleted successfully");
    }
}