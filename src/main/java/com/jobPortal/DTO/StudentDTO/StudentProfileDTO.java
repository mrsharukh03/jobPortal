package com.jobPortal.DTO.StudentDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileDTO {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be 10 to 15 digits")
    private String phone;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "Male|Female|Other", message = "Gender must be Male, Female, or Other")
    private String gender;

    @URL(message = "LinkedIn profile must be a valid URL")
    private String linkedinProfile;

    @URL(message = "GitHub profile must be a valid URL")
    private String githubProfile;

    private String publicId;

    @NotEmpty(message = "At least one skill must be selected")
    private List<@NotBlank(message = "Skill cannot be blank") String> skills;

    @NotEmpty(message = "Education list cannot be empty")
    private List<@Valid EducationDTO> educationList;
}

