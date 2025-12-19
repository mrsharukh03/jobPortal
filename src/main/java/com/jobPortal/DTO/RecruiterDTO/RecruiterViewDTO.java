package com.jobPortal.DTO.RecruiterDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterViewDTO {

    private UUID id;
    private String phone;
    private String companyLogoUrl;
    private String linkedInProfile;
    private String companyWebsite;
    private String companyName;
    private String designation;
    private String location;
    private String industry;
    private String companySize;
    private String companyDescription;
    private int yearsOfExperience;
    private String about;
    private List<String> hiringSkills;
    private boolean isProfileComplete;
    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
}
