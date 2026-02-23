package com.jobPortal.DTO.JobSeekerDTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EducationDTO {
    @NotBlank(message = "Degree is required")
    private String degree;

    @NotBlank(message = "Field of study is required")
    private String fieldOfStudy;

    @NotBlank(message = "College name is required")
    private String collegeName;

    @Min(value = 1950, message = "Invalid Start Year")
    private int startYear;

    private int endYear;
    private String country;

    @NotBlank(message = "Grade type is required")
    private String gradeType; // CGPA / Percentage

    @NotBlank(message = "Grade value is required")
    private String gradeValue;
}