package com.jobPortal.DTO.JobSeekerDTO;

import com.jobPortal.Enums.ApplicationStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobApplicationListDTO {
    private Long applicationId;
    private String jobTitle;
    private String companyName;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private Integer aiMatchScore;

    public JobApplicationListDTO(Long id, String title, String companyName,ApplicationStatus status, LocalDateTime appliedAt, @Min(0) @Max(100) Integer aiMatchScore) {

    this.applicationId = id;
        this.jobTitle = title;
        this.companyName = companyName;
        this.appliedAt = appliedAt;
        this.aiMatchScore = aiMatchScore;
        this.status = status;
}}
