package com.jobPortal.DTO;

import com.jobPortal.DTO.MultiUseDTO.SkillDTO;
import com.jobPortal.Enums.JobStatus;
import com.jobPortal.Enums.JobType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostDTO {

    // ===== Job Info =====
    @NotBlank(message = "Job title is required")
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;

    @NotBlank(message = "Job description is required")
    @Size(max = 3000, message = "Description must be less than 3000 characters")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Job type is required")
    private JobType type;

    @NotBlank(message = "Category is required")
    private String category;

    // ===== Salary =====
    @Min(value = 0, message = "Minimum salary cannot be negative")
    private Integer minSalary;

    @Min(value = 0, message = "Maximum salary cannot be negative")
    private Integer maxSalary;

    @NotBlank(message = "Experience requirement is required")
    private String experienceRequired;

    // ===== Company Info =====
    @NotBlank(message = "Company name is required")
    private String companyName;

    private String companyLogoUrl;

    // ===== Status =====
    private JobStatus status = JobStatus.OPEN;

    private boolean isActive = true;

    // ===== Dates =====
    @FutureOrPresent(message = "Last date to apply cannot be in the past")
    private LocalDate lastDateToApply;

    // ===== Skills =====
    @NotEmpty(message = "At least one skill is required")
    @Valid
    private List<SkillDTO> requiredSkills;

    // ===== Admin / Analytics / Premium Hooks =====
    private boolean featured = false;
    private int priorityScore = 0;
}