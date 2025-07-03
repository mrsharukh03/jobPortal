package com.jobPortal.DTO.StudentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class StudentProfileDTO {
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private String linkedinProfile;
    private String githubProfile;
    private String publicId;
    private List<String> skills;
    private List<EducationDTO> educationList;
}
