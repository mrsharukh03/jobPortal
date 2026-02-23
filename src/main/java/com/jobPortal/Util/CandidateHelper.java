package com.jobPortal.Util;

import com.jobPortal.Model.Users.Seeker;

public class CandidateHelper {

    public static int calculateProfileCompletion(Seeker seeker) {
        int score = 0;

        // Personal Info
        if (seeker.getPhone() != null && !seeker.getPhone().isEmpty()) score += 3;
        if (seeker.getDOB() != null) score += 4;
        if (seeker.getGender() != null) score += 3;

        // Bio & Career Objective
        if (seeker.getBio() != null && !seeker.getBio().isEmpty()) score += 5;
        if (seeker.getCareerObjective() != null && !seeker.getCareerObjective().isEmpty()) score += 5;

        // Location
        if (seeker.getCurrentLocation() != null && !seeker.getCurrentLocation().isEmpty()) score += 5;
        if (seeker.getPreferredLocations() != null && !seeker.getPreferredLocations().isEmpty()) score += 5;

        // Social/Portfolio
        if (seeker.getLinkedinProfile() != null && !seeker.getLinkedinProfile().isEmpty()) score += 4;
        if (seeker.getGithubProfile() != null && !seeker.getGithubProfile().isEmpty()) score += 3;
        if (seeker.getPortfolioUrl() != null && !seeker.getPortfolioUrl().isEmpty()) score += 3;

        // Skills
        if (seeker.getSkills() != null && !seeker.getSkills().isEmpty()) score += 10;
        if (seeker.getAiRecommendedSkills() != null && !seeker.getAiRecommendedSkills().isEmpty()) score += 5;

        // Experience & Education
        if (seeker.getExperienceList() != null && !seeker.getExperienceList().isEmpty()) score += 15;
        if (seeker.getEducationList() != null && !seeker.getEducationList().isEmpty()) score += 10;

        // Certifications
        if (seeker.getCertifications() != null && !seeker.getCertifications().isEmpty()) score += 5;

        // Resume & AI Summary
        if (seeker.getResumeUrl() != null && !seeker.getResumeUrl().isEmpty()) score += 5;
        if (seeker.getResumeText() != null && !seeker.getResumeText().isEmpty()) score += 5;
        if (seeker.getAiSummary() != null && !seeker.getAiSummary().isEmpty()) score += 5;

        // Languages & Soft Skills
        if (seeker.getLanguages() != null && !seeker.getLanguages().isEmpty()) score += 5;
        if (seeker.getSoftSkills() != null && !seeker.getSoftSkills().isEmpty()) score += 5;

        return score; // out of 100
    }
}
