package com.jobPortal.DTO.RecruiterDTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterProfileDTO {

    // ===== Contact Info =====
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be between 10 to 15 digits")
    private String phone;

    // ===== Profile Images =====
    @URL(message = "Profile image URL must be valid")
    private String profileImageUrl;

    @URL(message = "Company logo URL must be valid")
    private String companyLogoUrl;

    // ===== Social Profiles =====
    @URL(message = "LinkedIn profile must be a valid URL")
    private String linkedInProfile;

    @URL(message = "Company website must be a valid URL")
    private String companyWebsite;

    // ===== Company Info =====
    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Industry is required")
    private String industry;

    private String companySize; // Startup, 1-50, 50-200, 500+

    @Size(max = 500, message = "Company description cannot exceed 500 characters")
    private String companyDescription;

    // ===== Recruiter Info =====
    @Min(value = 0, message = "Experience must be positive")
    private int yearsOfExperience;

    @Size(max = 300, message = "About section cannot exceed 300 characters")
    private String about;

    private List<String> hiringSkills; // Java, React, HR, Sales
}
