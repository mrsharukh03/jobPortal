package com.jobPortal.Service;

import com.jobPortal.DTO.RecruiterDTO.SavedJobResponse;
import com.jobPortal.Exception.BusinessException;
import com.jobPortal.Model.JobPost;
import com.jobPortal.Model.Seeker.SavedJob;
import com.jobPortal.Model.Users.Seeker;
import com.jobPortal.Repository.JobRepository;
import com.jobPortal.Repository.SavedJobRepository;
import com.jobPortal.Repository.SeekerRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final SeekerRepository seekerRepository;
    private final JobRepository jobRepository;

    @Transactional
    public boolean saveJob(UUID userId, Long jobId) {

        Seeker seeker = seekerRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Seeker profile not found"));

        if (savedJobRepository.findByUserIdAndJobId(userId, jobId).isPresent()) {
            throw new BusinessException("Job already bookmarked");
        }

        JobPost job = jobRepository.findById(jobId)
                .orElseThrow(() -> new BusinessException("Job not found"));

        SavedJob savedJob = SavedJob.builder()
                .user(seeker.getUser())
                .job(job)
                .savedAt(LocalDateTime.now())
                .build();

        savedJobRepository.save(savedJob);
        return true;
    }

    @Transactional
    public boolean unsaveJob(UUID userId, Long jobId) {
        try {
            int response = savedJobRepository.deleteByUserIdAndJobId(userId, jobId);
            if (response == 0) {
                throw new BusinessException("No saved job found to delete");
            }
            return true;
        } catch (Exception e) {
            throw new BusinessException("Error while unsaving job: " + e.getMessage());
        }
    }

    public List<SavedJobResponse> getSavedJobs(UUID userId) {
        List<SavedJob> savedJobs = savedJobRepository.findByUserId(userId);


        return savedJobs.stream().map(savedJob -> {
            SavedJobResponse res = new SavedJobResponse();
            res.setJobId(savedJob.getJob().getId());
            res.setJobTitle(savedJob.getJob().getTitle());
            res.setCompanyName(savedJob.getJob().getCompanyName());
            res.setLocation(savedJob.getJob().getLocation());
            res.setSalary(savedJob.getJob().getMaxSalary());
            res.setPostedAt(savedJob.getJob().getPostedDate());
            res.setJobStatus(savedJob.getJob().getStatus());
            res.setSavedAt(savedJob.getSavedAt());
            return res;
        }).toList();
    }
}