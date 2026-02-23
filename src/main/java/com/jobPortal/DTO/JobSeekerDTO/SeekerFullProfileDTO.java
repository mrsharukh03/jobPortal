package com.jobPortal.DTO.JobSeekerDTO;

import com.jobPortal.Model.Seeker.Certification;
import com.jobPortal.Model.Seeker.Education;
import com.jobPortal.Model.Seeker.Experience;
import com.jobPortal.Model.Skill;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class SeekerFullProfileDTO {
    // User Info
    private UUID userId;
    private String fullName;
    private String email;
    private List<String> role;
    private boolean isActive;
    private boolean isVerified;
    private String profileUrl;

    // Personal Info
    private String phone;
    private String gender;
    private LocalDate DOB;
    private String bio;
    private String careerObjective;
    private String currentLocation;
    private List<String> preferredLocations;
    private List<String> languages;
    private boolean currentlyWorking;
    private String noticePeriod;
    private Double expectedSalary;

    // Social / Portfolio
    private String linkedinProfile;
    private String githubProfile;
    private String portfolioUrl;

    // Resume / AI
    private String resumeUrl;
    private String resumeText;
    private String aiSummary;
    private List<String> aiRecommendedSkills;
    private List<SkillResponse> skills;
    private List<String> softSkills;

    // Experience / Education / Certifications
    private List<ExperienceResponse> experienceList;
    private List<EducationResponse> educationList;
    private List<CertificationResponse> certifications;

    // Image
    private String profileImage; // Base64

    // Profile Completion
    private int profileCompletion;
    private boolean isProfileComplete;
}
