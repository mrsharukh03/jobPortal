package com.jobPortal.DTO.RecruiterDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder @NoArgsConstructor
@AllArgsConstructor
public class RecruiterDashboardOverviewDTO {

    // Jobs
    private long totalJobs;
    private long activeJobs;
    private long closedJobs;
    private long expiredJobs;
    private long deletedJobs;

    // Applications
    private long totalApplications;
    private long pendingApplications;
    private long shortlisted;
    private long interviewScheduled;
    private long selected;
    private long rejected;

    // Performance
    private double selectionRate;       // selected / totalApplications * 100
    private double interviewRate;       // interviewScheduled / totalApplications * 100
    private double rejectionRate;

    // Engagement
    private long totalViews;
    private double avgApplicationsPerJob;

    // AI
    private double avgAiMatchScore;

    // Hiring
    private long totalHires;
}