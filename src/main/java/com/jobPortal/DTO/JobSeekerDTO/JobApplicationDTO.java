package com.jobPortal.DTO.JobSeekerDTO;

import com.jobPortal.Enums.ApplicationStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class JobApplicationDTO {

    private Long applicationId;

    // Job info
    private String jobTitle;
    private String companyName;
    private String location;
    private String jobType;
    private LocalDate lastDateToApply;

    // Application info
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime lastUpdatedAt;

    // Candidate submitted data
    private String resumeUrl;
    private String coverLetter;

    // AI features
    private String aiSummary;
    private Integer aiMatchScore;

    // Interview info
    private LocalDate interviewDate;
    private String interviewFeedback;

    // Rejection info
    private String rejectionReason;
}
