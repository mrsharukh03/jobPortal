package com.jobPortal.DTO;


import com.jobPortal.Enums.JobStatus;
import com.jobPortal.Enums.JobType;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class JobListDTO {
    private Long id;
    private String title;
    private String companyName;
    private String location;
    private JobType type;
    private Integer minSalary;
    private Integer maxSalary;
    private JobStatus status;
    private Long viewCount;
}