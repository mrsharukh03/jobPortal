package com.jobPortal.DTO.RecruiterDTO;

import com.jobPortal.Enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationListDTO {

    private Long applicationId;
    private Long jobPostId;
    private String jobTitle;

    private String candidateName;
    private String location;

    private LocalDateTime appliedAt;
    private ApplicationStatus status;

    private Integer aiMatchScore;
    private String aiSummary;

}