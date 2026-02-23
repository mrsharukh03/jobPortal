package com.jobPortal.DTO;

import com.jobPortal.Enums.JobStatus;
import com.jobPortal.Enums.JobType;
import lombok.Data;

@Data
public class JobSearchFilterDTO {

    private String keyword;
    private String location;
    private String category;
    private JobType type;

    private Integer minSalary;
    private Integer maxSalary;

    private String experienceRequired;
    private JobStatus status;

    private Boolean featured;

    // Sorting
    private String sortBy = "postedDate";
    private String sortDir = "desc";

    // Pagination
    private int page = 0;
    private int size = 10;
}
