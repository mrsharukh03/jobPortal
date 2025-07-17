package com.jobPortal.DTO.MultiUseDTO;

import com.jobPortal.Enums.ApplicationStatus;
import com.jobPortal.Enums.JobType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data @NoArgsConstructor @AllArgsConstructor
public class ApplicationDTO {
    private Long applicationId;
    private Date appliedDate;
    private ApplicationStatus status;

    // From JobPost
    private Long jobId;
    private String jobTitle;
    private String companyName;      // From Recruiter
    private String location;
    private JobType type;             // e.g., Full-Time, Internship
    private int salary;
    private String coverLetter;      // Optional, for info

    // Optional (extra UX)
    private Date lastDateToApply;
}
