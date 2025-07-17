package com.jobPortal.DTO.RecruiterDTO;

import com.jobPortal.Enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationRecruiterViewDTO {

        private Long applicationId;

        // Job post related
        private String jobPostId;
        private String jobPostName;

        // Student basic info
        private String studentName;
        private String studentEmail;
        private List<String> skills;

        // Portfolio Links
        private String resumeUrl;
        private String linkedinUrl;
        private String githubUrl;

        // Application metadata
        private Date appliedDate = new Date();
        private ApplicationStatus status = ApplicationStatus.PENDING;
        private boolean selection = false;
        private String coverLetter;
}
