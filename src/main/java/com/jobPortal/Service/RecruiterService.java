package com.jobPortal.Service;

import com.jobPortal.DTO.RecruiterDTO.RecruiterDashboardOverviewDTO;
import com.jobPortal.DTO.RecruiterDTO.RecruiterProfileDTO;
import com.jobPortal.DTO.RecruiterDTO.RecruiterViewDTO;
import com.jobPortal.Enums.ApplicationStatus;
import com.jobPortal.Enums.JobStatus;
import com.jobPortal.Exception.BusinessException;
import com.jobPortal.Model.Users.Recruiter;
import com.jobPortal.Repository.JobApplicationRepository;
import com.jobPortal.Repository.JobRepository;
import com.jobPortal.Repository.RecruiterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Service
public class RecruiterService {

    private static final Logger log = LoggerFactory.getLogger(RecruiterService.class);
    // Unused UserRepository removed for cleaner code
    private final RecruiterRepository recruiterRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;

    public RecruiterService(RecruiterRepository recruiterRepository,
                            JobRepository jobRepository,
                            JobApplicationRepository jobApplicationRepository) {
        this.recruiterRepository = recruiterRepository;
        this.jobRepository = jobRepository;
        this.jobApplicationRepository = jobApplicationRepository;
    }

    public boolean updateProfile(UUID userId, RecruiterProfileDTO dto) {
        Optional<Recruiter> recruiteropt = recruiterRepository.findById(userId);
        if (recruiteropt.isEmpty()) {
            throw new BusinessException("Recruiter profile not found");
        }

        Recruiter recruiter = recruiteropt.get();

        recruiter.setPhone(dto.getPhone());
        recruiter.setCompanyLogoUrl(dto.getCompanyLogoUrl());
        recruiter.setLinkedInProfile(dto.getLinkedInProfile());
        recruiter.setCompanyWebsite(dto.getCompanyWebsite());

        recruiter.setCompanyName(dto.getCompanyName());
        recruiter.setDesignation(dto.getDesignation());
        recruiter.setLocation(dto.getLocation());
        recruiter.setIndustry(dto.getIndustry());
        recruiter.setCompanySize(dto.getCompanySize());
        recruiter.setCompanyDescription(dto.getCompanyDescription());

        recruiter.setYearsOfExperience(dto.getYearsOfExperience());
        recruiter.setAbout(dto.getAbout());
        recruiter.setHiringSkills(dto.getHiringSkills());

        recruiter.setProfileComplete(true);
        recruiter.setUpdateTime(LocalDateTime.now());

        recruiterRepository.save(recruiter);
        return true;
    }

    public RecruiterViewDTO getProfile(UUID userId) {
        Optional<Recruiter> recruiteropt = recruiterRepository.findById(userId);
        if (recruiteropt.isEmpty()) {
            throw new BusinessException("Recruiter profile not found");
        }

        Recruiter recruiter = recruiteropt.get();

        return new RecruiterViewDTO(
                recruiter.getId(),
                recruiter.getPhone(),
                recruiter.getCompanyLogoUrl(),
                recruiter.getLinkedInProfile(),
                recruiter.getCompanyWebsite(),
                recruiter.getCompanyName(),
                recruiter.getDesignation(),
                recruiter.getLocation(),
                recruiter.getIndustry(),
                recruiter.getCompanySize(),
                recruiter.getCompanyDescription(),
                recruiter.getYearsOfExperience(),
                recruiter.getAbout(),
                recruiter.getHiringSkills(),
                recruiter.isProfileComplete(),
                recruiter.getCreatedTime(),
                recruiter.getUpdateTime()
        );
    }

    public RecruiterDashboardOverviewDTO getDashboardOverview(UUID userId) {

        Recruiter recruiter = recruiterRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Recruiter profile not found"));

        UUID recruiterId = recruiter.getId();

        // ===== OPTIMIZED DATABASE CALLS =====
        // Ab hum individually count nahi laayenge, ek sath le aayenge
        List<Object[]> jobCountsByStatus = jobRepository.countJobsByStatusForRecruiter(recruiterId);
        List<Object[]> appCountsByStatus = jobApplicationRepository.countApplicationsByStatusForRecruiter(recruiterId);

        // ===== VARIABLES INITIALIZATION =====
        long totalJobs = 0, activeJobs = 0, closedJobs = 0, expiredJobs = 0, deletedJobs = 0;
        long totalApplications = 0, pending = 0, shortlisted = 0, interview = 0, selected = 0, rejected = 0;

        // ===== PROCESS JOB COUNTS (JAVA MEMORY ME) =====
        for (Object[] row : jobCountsByStatus) {
            JobStatus status = (JobStatus) row[0];
            long count = ((Number) row[1]).longValue();

            totalJobs += count; // Total yahin calculate ho jayega

            if (status == JobStatus.OPEN) activeJobs = count;
            else if (status == JobStatus.CLOSED) closedJobs = count;
            else if (status == JobStatus.EXPIRED) expiredJobs = count;
            else if (status == JobStatus.DELETED) deletedJobs = count;
        }

        // ===== PROCESS APPLICATION COUNTS (JAVA MEMORY ME) =====
        for (Object[] row : appCountsByStatus) {
            ApplicationStatus status = (ApplicationStatus) row[0];
            long count = ((Number) row[1]).longValue();

            totalApplications += count; // Total yahin calculate ho jayega

            if (status == ApplicationStatus.PENDING) pending = count;
            else if (status == ApplicationStatus.SHORTLISTED) shortlisted = count;
            else if (status == ApplicationStatus.INTERVIEW_SCHEDULED) interview = count;
            else if (status == ApplicationStatus.SELECTED) selected = count;
            else if (status == ApplicationStatus.REJECTED) rejected = count;
        }

        // ===== OTHER METRICS (These still need their own queries) =====
        long totalViews = jobRepository.sumViewsByRecruiter(recruiterId);

        Double avgAiMatchScore = jobApplicationRepository.avgAiScoreByRecruiter(recruiterId);
        if (avgAiMatchScore == null) avgAiMatchScore = 0.0;

        // ===== CALCULATIONS =====
        double avgApplicationsPerJob = totalJobs == 0 ? 0 : (double) totalApplications / totalJobs;
        double selectionRate = totalApplications == 0 ? 0 : ((double) selected / totalApplications) * 100;
        double interviewRate = totalApplications == 0 ? 0 : ((double) interview / totalApplications) * 100;
        double rejectionRate = totalApplications == 0 ? 0 : ((double) rejected / totalApplications) * 100;

        // ===== BUILD RESPONSE =====
        return RecruiterDashboardOverviewDTO.builder()
                .totalJobs(totalJobs)
                .activeJobs(activeJobs)
                .closedJobs(closedJobs)
                .expiredJobs(expiredJobs)
                .deletedJobs(deletedJobs)

                .totalApplications(totalApplications)
                .pendingApplications(pending)
                .shortlisted(shortlisted)
                .interviewScheduled(interview)
                .selected(selected)
                .rejected(rejected)

                .selectionRate(round(selectionRate))
                .interviewRate(round(interviewRate))
                .rejectionRate(round(rejectionRate))

                .totalViews(totalViews)
                .avgApplicationsPerJob(round(avgApplicationsPerJob))
                .avgAiMatchScore(round(avgAiMatchScore))

                .totalHires(selected)
                .build();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}