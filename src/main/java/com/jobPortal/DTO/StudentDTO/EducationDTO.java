package com.jobPortal.DTO.StudentDTO;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EducationDTO {

    @NotBlank(message = "Degree is required")
    private String degree;

    @NotBlank(message = "Field of study is required")
    private String fieldOfStudy;

    @NotBlank(message = "College name is required")
    private String collegeName;

    @NotBlank(message = "Country is required")
    private String country;

    @Min(value = 1950, message = "Start year must be after 1950")
    @Max(value = 2100, message = "Start year seems invalid")
    private int startYear;

    @Min(value = 1950, message = "End year must be after 1950")
    @Max(value = 2100, message = "End year seems invalid")
    private int endYear;

    @NotBlank(message = "Grade type is required")
    private String gradeType;

    @NotBlank(message = "Grade value is required")
    private String gradeValue;
}
