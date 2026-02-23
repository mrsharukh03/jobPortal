package com.jobPortal.DTO;

import com.jobPortal.Enums.JobStatus;
import com.jobPortal.Enums.JobType;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class JobRequestDTO {

    private Long id;
    private String title;
    private String description;
    private JobType type;
    private String category;
    private Integer minSalary;
    private Integer maxSalary;
    private String experienceRequired;
    private String companyName;
    private String companyLogoUrl;
    private JobStatus status = JobStatus.OPEN;
    private boolean isActive = true;
    private LocalDate postedDate;
    private LocalDate lastDateToApply;
    private List<String> requiredSkills;
    private boolean featured = false;
    private int priorityScore = 0;
}
