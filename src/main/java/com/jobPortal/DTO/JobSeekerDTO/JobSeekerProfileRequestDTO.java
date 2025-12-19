package com.jobPortal.DTO.JobSeekerDTO;

import com.jobPortal.Enums.MarriageStatus;
import com.jobPortal.Model.Skill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekerProfileRequestDTO implements Serializable {


    private String phone;
    private String gender;
    private Date DOB;
    private MarriageStatus marriageStatus;
    private String linkedinProfile;
    private String githubProfile;
    private String profileImage;
    private String resumeUrl;
    private List<Skill> skills;
    private List<EducationDTO> educationList;
}
