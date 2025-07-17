package com.jobPortal.DTO.RecruiterDTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterProfileDTO {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be between 10 to 15 digits")
    private String phone;

    @URL(message = "Profile image URL must be valid")
    private String profileImageUrl;

    @URL(message = "LinkedIn profile must be a valid URL")
    private String linkedInProfile;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotBlank(message = "Location is required")
    private String location;
}
