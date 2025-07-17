package com.jobPortal.DTO;
import com.jobPortal.Enums.JobStatus;
import com.jobPortal.Enums.JobType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class JobSummaryDTO {

    private Long id;
    private String title;
    private String location;
    private JobType type;
    private int salary;
    private String experienceRequired;

    private String companyName;
    private String companyLogoUrl;
    private String jobDescription;

    private List<String> requiredSkills;

    private Date postedDate;
    private Date expiryDate;
    private JobStatus jobStatus;
}
