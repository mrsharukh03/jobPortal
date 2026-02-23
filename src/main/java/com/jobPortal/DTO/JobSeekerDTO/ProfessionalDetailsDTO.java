package com.jobPortal.DTO.JobSeekerDTO;

import com.jobPortal.DTO.MultiUseDTO.SkillDTO;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import java.util.List;

@Data
public class ProfessionalDetailsDTO {

    private String bio;

    @URL(message = "Invalid LinkedIn URL")
    private String linkedinProfile;

    @URL(message = "Invalid GitHub URL")
    private String githubProfile;

    @URL(message = "Invalid Portfolio URL")
    private String portfolioUrl;

    @Min(value = 0, message = "Salary cannot be negative")
    private Double expectedSalary;

    private String noticePeriod;

    private List<String> languages;
    private List<String> preferredLocations;

    private List<SkillDTO> skills;
}