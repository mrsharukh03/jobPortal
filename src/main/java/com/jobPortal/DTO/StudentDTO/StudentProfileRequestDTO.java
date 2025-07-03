package com.jobPortal.DTO.StudentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileRequestDTO implements Serializable {
    private String name;
    private String email;
    private String phone;
    private String linkedinProfile;
    private String githubProfile;
    private String profileImage;
    private String resumeUrl;
    private List<String> skills;
    private List<EducationDTO> education;
}
