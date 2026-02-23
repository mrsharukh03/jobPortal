package com.jobPortal.DTO.RecruiterDTO;

import com.jobPortal.Enums.JobStatus;
import lombok.Data;
import java.time.LocalDateTime;


@Data
public class SavedJobResponse {

    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String location;
    private Integer salary;
    private JobStatus jobStatus;
    private LocalDateTime postedAt;
    private LocalDateTime savedAt;
}