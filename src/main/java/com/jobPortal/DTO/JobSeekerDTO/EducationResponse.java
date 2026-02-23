package com.jobPortal.DTO.JobSeekerDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EducationResponse {
    private Long id;
    private String degree;
    private String fieldOfStudy;
    private String collegeName;
    private String country;
    private int startYear;
    private int endYear;
    private String gradeType;
    private String gradeValue;
}