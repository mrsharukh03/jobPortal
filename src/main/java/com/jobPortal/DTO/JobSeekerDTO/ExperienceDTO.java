package com.jobPortal.DTO.JobSeekerDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ExperienceDTO {
    @NotBlank(message = "Job Title is required")
    private String jobTitle;

    @NotBlank(message = "Company Name is required")
    private String companyName;

    @NotNull(message = "Start Date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    private String description;
}