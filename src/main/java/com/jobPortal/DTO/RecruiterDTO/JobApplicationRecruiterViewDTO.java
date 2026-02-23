package com.jobPortal.DTO.RecruiterDTO;

import com.jobPortal.Enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JobApplicationRecruiterViewDTO {

        private Long applicationId;

        private Long jobPostId;
        private String jobTitle;

        // Candidate Info
        private String candidateName;
        private String email;
        private String phone;
        private String location;
        private Integer totalExperience;

        private List<String> skills;

        // Portfolio
        private String resumeUrl;
        private String linkedinUrl;
        private String githubUrl;
        private String coverLetter;

        // AI
        private Integer aiMatchScore;
        private String aiSummary;

        // Application Info
        private LocalDateTime appliedAt;
        private ApplicationStatus status;
        private Integer rating;
        private String recruiterNotes;
        private Long viewCount;
}
