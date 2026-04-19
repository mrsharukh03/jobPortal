package com.jobPortal.Service;

import com.jobPortal.DTO.JobListDTO;
import com.jobPortal.DTO.JobPostDTO;
import com.jobPortal.DTO.JobRequestDTO;
import com.jobPortal.DTO.JobSearchFilterDTO;
import com.jobPortal.DTO.MultiUseDTO.SkillResponse;
import com.jobPortal.DTO.RecruiterDTO.BulkUpdateApplicationStatusDTO;
import com.jobPortal.DTO.RecruiterDTO.JobApplicationListDTO;
import com.jobPortal.DTO.RecruiterDTO.JobApplicationRecruiterViewDTO;
import com.jobPortal.DTO.RecruiterDTO.UpdateApplicationStatusDTO;
import com.jobPortal.Enums.ApplicationStatus;
import com.jobPortal.Enums.JobStatus;
import com.jobPortal.Exception.BadRequestException;
import com.jobPortal.Exception.BusinessException;
import com.jobPortal.Mapper.JobSpecification;
import com.jobPortal.Model.JobApplication;
import com.jobPortal.Model.JobPost;
import com.jobPortal.Model.Skill;
import com.jobPortal.Model.Users.Recruiter;
import com.jobPortal.Model.Users.Seeker;
import com.jobPortal.Model.Users.User;
import com.jobPortal.Repository.JobApplicationRepository;
import com.jobPortal.Repository.JobRepository;
import com.jobPortal.Repository.RecruiterRepository;
import com.jobPortal.Repository.SkillRepository;
import com.jobPortal.Util.ApplicationHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    private final RecruiterRepository recruiterRepository;
    private final JobRepository jobPostRepository;
    private final ModelMapper modelMapper;
    private final JobApplicationRepository jobApplicationRepository;
    private final SkillRepository skillRepository;

    @Transactional
    public boolean postJob(String email, JobPostDTO jobPostDTO) {

        Optional<Recruiter> recruiterOpt = recruiterRepository.findByUser_Email(email);
        if (recruiterOpt.isEmpty()) {
            throw new BusinessException("You are not allowed to post jobs.");
        }

        Recruiter recruiter = recruiterOpt.get();

        if (jobPostDTO.getMinSalary() != null && jobPostDTO.getMaxSalary() != null &&
                jobPostDTO.getMaxSalary() < jobPostDTO.getMinSalary()) {
            throw new BusinessException("Max salary cannot be less than min salary.");
        }

        if (jobPostDTO.getLastDateToApply() != null &&
                jobPostDTO.getLastDateToApply().isBefore(LocalDate.now())) {
            throw new BusinessException("Last date to apply cannot be in the past.");
        }

        JobPost jobPost = modelMapper.map(jobPostDTO, JobPost.class);

        jobPost.setRecruiter(recruiter);
        jobPost.setViewCount(0);
        jobPost.setApplicationsCount(0);
        jobPost.setActive(true);
        jobPost.setPostedDate(LocalDateTime.now());
        if (jobPost.getStatus() == null) {
            jobPost.setStatus(JobStatus.OPEN);
        }

        List<Skill> managedSkills = new ArrayList<>();

        if (jobPostDTO.getRequiredSkills() != null) {
            for (SkillResponse skillDto : jobPostDTO.getRequiredSkills()) {
                if (skillDto.getName() == null || skillDto.getName().trim().isEmpty()) {
                    continue;
                }
                String normalizedName = skillDto.getName().trim().toLowerCase();

                Skill skill = skillRepository.findByNameIgnoreCase(normalizedName)
                        .orElseGet(() -> {
                            Skill newSkill = new Skill();
                            newSkill.setName(normalizedName);
                            return skillRepository.save(newSkill);
                        });

                managedSkills.add(skill);
            }
        }

        jobPost.setRequiredSkills(managedSkills);

        JobPost savedJob = jobPostRepository.save(jobPost);
        recruiter.getJobPosts().add(savedJob);
        recruiter.setTotalJobsPosted(recruiter.getJobPosts().size());

        return true;
    }

    @Transactional
    public boolean updatePost(String recruiterEmail, Long postId, JobPostDTO dto) {

        Recruiter recruiter = recruiterRepository.findByUser_Email(recruiterEmail)
                .orElseThrow(() -> new BadRequestException("You are not allowed to update jobs."));

        JobPost jobPost = jobPostRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("Job post not found"));

        if (!jobPost.getRecruiter().getId().equals(recruiter.getId())) {
            throw new BadRequestException("You are not allowed to update this job.");
        }

        if (dto.getMinSalary() != null && dto.getMaxSalary() != null
                && dto.getMaxSalary() < dto.getMinSalary()) {
            throw new BusinessException("Max salary cannot be less than min salary.");
        }

        if (dto.getLastDateToApply() != null &&
                dto.getLastDateToApply().isBefore(LocalDate.now())) {
            throw new BusinessException("Last date cannot be in the past.");
        }

        jobPost.setTitle(dto.getTitle());
        jobPost.setDescription(dto.getDescription());
        jobPost.setLocation(dto.getLocation());
        jobPost.setType(dto.getType());
        jobPost.setCategory(dto.getCategory());
        jobPost.setMinSalary(dto.getMinSalary());
        jobPost.setMaxSalary(dto.getMaxSalary());
        jobPost.setExperienceRequired(dto.getExperienceRequired());
        jobPost.setLastDateToApply(dto.getLastDateToApply());

        if (dto.getRequiredSkills() != null) {
            List<Skill> skills = dto.getRequiredSkills().stream()
                    .filter(skillName -> skillName != null && !skillName.getName().isBlank())
                    .map(name -> {
                        Skill skill = new Skill();
                        skill.setName(name.getName());
                        return skill;
                    })
                    .toList();

            jobPost.setRequiredSkills(skills);
        }

        jobPostRepository.save(jobPost);
        return true;
    }

    public Page<JobListDTO> getJobsByRecruiterUserId(String email, int page, int size) {
        recruiterRepository.findByUser_Email(email)
                .orElseThrow(() -> new BadRequestException("You are not allowed to view jobs."));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postedDate"));
        Page<JobPost> jobs = jobPostRepository.findAll(JobSpecification.recruiterJobs(email), pageable);
        return jobs.map(job -> modelMapper.map(job, JobListDTO.class));
    }

    @Transactional
    public Page<JobApplicationListDTO> getApplicationsByJobId(UUID userId, Long jobId, int page, int size) {
        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Applications not found for this job"));

        JobPost jobPost = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new BusinessException("Job not found"));

        if (!jobPost.getRecruiter().getId().equals(recruiter.getId())) {
            throw new BadRequestException("Application not found for this job");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedAt"));
        Page<JobApplication> applications = jobApplicationRepository.findByJobPost_Id(jobId, pageable);

        return applications.map(application -> {
            Seeker seeker = application.getSeeker();
            User user = seeker.getUser();

            JobApplicationListDTO dto = new JobApplicationListDTO();
            dto.setApplicationId(application.getId());
            dto.setJobPostId(application.getJobPost().getId());
            dto.setJobTitle(application.getJobPost().getTitle());
            dto.setCandidateName(user.getFullName());
            dto.setLocation(seeker.getCurrentLocation());
            dto.setAppliedAt(application.getAppliedAt());
            dto.setStatus(application.getStatus());
            dto.setAiMatchScore(application.getAiMatchScore());
            dto.setAiSummary(application.getAiSummary());
            return dto;
        });
    }

    @Transactional
    public JobApplicationRecruiterViewDTO getApplicationByApplicationId(UUID userId, Long applicationId) {
        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("You are not allowed to view applications"));

        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException("Application not found"));

        JobPost jobPost = application.getJobPost();
        if (!jobPost.getRecruiter().getId().equals(recruiter.getId())) {
            throw new BadRequestException("You are not allowed to view this application");
        }

        Seeker seeker = application.getSeeker();
        User user = seeker.getUser();

        JobApplicationRecruiterViewDTO dto = new JobApplicationRecruiterViewDTO();
        dto.setApplicationId(application.getId());
        if (application.getAppliedAt() != null)
            dto.setAppliedAt(application.getAppliedAt());
        else
            dto.setAppliedAt(LocalDateTime.now());
        dto.setStatus(application.getStatus());
        dto.setAiMatchScore(application.getAiMatchScore());
        dto.setAiSummary(application.getAiSummary());
        dto.setRating(application.getRating());
        dto.setRecruiterNotes(application.getRecruiterNotes());

        dto.setJobPostId(jobPost.getId());
        dto.setJobTitle(jobPost.getTitle());
        dto.setViewCount(jobPost.getViewCount());
        dto.setCandidateName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(seeker.getPhone());
        dto.setLocation(seeker.getCurrentLocation());

        int totalExperience = seeker.getExperienceList().stream()
                .mapToInt(exp -> {
                    LocalDate start = exp.getStartDate();
                    LocalDate end = exp.getEndDate() != null ? exp.getEndDate() : LocalDate.now();
                    return start != null ? Period.between(start, end).getYears() : 0;
                }).sum();

        dto.setTotalExperience(totalExperience);
        dto.setSkills(seeker.getSkills().stream().map(Skill::getName).toList());

        dto.setResumeUrl(application.getResumeUrl() != null ? application.getResumeUrl() : "Not Provided");
        dto.setLinkedinUrl(seeker.getLinkedinProfile());
        dto.setGithubUrl(seeker.getGithubProfile() != null ? seeker.getGithubProfile() : "");
        dto.setCoverLetter(application.getCoverLetter() != null ? application.getCoverLetter() : "Not Provided");

        return dto;
    }

    public JobRequestDTO getJobById(Long jobId) {
        Optional<JobPost> jobOpt = jobPostRepository.findOne(
                JobSpecification.publicVisible()
                        .and((root, query, cb) -> cb.equal(root.get("id"), jobId))
        );
        if (jobOpt.isEmpty()) {
            return null;
        }
        JobPost job = jobOpt.get();

        job.setViewCount(job.getViewCount() + 1);
        jobPostRepository.save(job);
        return modelMapper.map(job, JobRequestDTO.class);
    }

    public Page<JobListDTO> getPopularJobs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "applicationsCount"));
        Page<JobPost> jobs = jobPostRepository.findAll(JobSpecification.publicVisible(), pageable);
        return jobs.map(job -> modelMapper.map(job, JobListDTO.class));
    }
    public Page<JobListDTO> getPersonalizedJobs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "applicationsCount"));
        Page<JobPost> jobs = jobPostRepository.findAll(JobSpecification.publicVisible(), pageable);
        return jobs.map(job -> modelMapper.map(job, JobListDTO.class));
    }

    public List<JobListDTO> findJobsByCategory(String category) {
        var spec = JobSpecification.publicVisible()
                .and((root, query, cb) -> cb.equal(cb.lower(root.get("category")), category.toLowerCase()));
        List<JobPost> jobs = jobPostRepository.findAll(spec);
        return jobs.stream().map(job -> modelMapper.map(job, JobListDTO.class)).toList();
    }

    @Transactional
    public void updateApplicationStatus(UUID recruiterId, Long applicationId, UpdateApplicationStatusDTO dto) {
        Recruiter recruiter = recruiterRepository.findByUserId(recruiterId)
                .orElseThrow(() -> new BadRequestException("You are not allowed to update applications"));

        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException("Application not found"));

        if (!application.getJobPost().getRecruiter().getId().equals(recruiter.getId())) {
            throw new BadRequestException("You are not allowed to update this application");
        }

        ApplicationStatus currentStatus = application.getStatus();
        ApplicationStatus newStatus = dto.getStatus();

        if (!(ApplicationHelper.canMoveForward(currentStatus, newStatus))) {
            throw new BadRequestException("Cannot move application backward in status.");
        }

        if (currentStatus == newStatus) {
            throw new BadRequestException("Application is already in the desired status.");
        }

        if (currentStatus.equals(ApplicationStatus.INTERVIEW_SCHEDULED)){
            if (dto.getInterviewDate() == null)
                throw new BusinessException("Interview date and time required for scheduling interview");
            application.setInterviewDate(dto.getInterviewDate());
            application.setInterviewDate(LocalDate.now());
        }

        if(currentStatus.equals(ApplicationStatus.SELECTED)){
            application.setInterviewDate(LocalDate.now());
        }
        if (currentStatus.equals(ApplicationStatus.SHORTLISTED)){
            application.setShortlistedAt(LocalDateTime.now());
        }

        if (currentStatus.equals(ApplicationStatus.REJECTED)){
            application.setRejectedAt(LocalDateTime.now());
        }

        application.setStatus(newStatus);
        if (dto.getRecruiterNotes() != null) {
            application.setRecruiterNotes(dto.getRecruiterNotes());
        }

        jobApplicationRepository.save(application);
    }

    @Transactional
    public void bulkUpdateApplicationStatus(UUID recruiterId, BulkUpdateApplicationStatusDTO dto) {
        Recruiter recruiter = recruiterRepository.findByUserId(recruiterId)
                .orElseThrow(() -> new BadRequestException("You are not allowed to update applications"));

        List<JobApplication> applications = jobApplicationRepository.findAllById(dto.getApplicationIds());

        for (JobApplication application : applications) {
            if (!application.getJobPost().getRecruiter().getId().equals(recruiter.getId())) {
                continue;
            }

            ApplicationStatus currentStatus = application.getStatus();
            ApplicationStatus newStatus = dto.getStatus();

            if (!(ApplicationHelper.canMoveForward(currentStatus, newStatus))) {
                continue;
            }
            if (currentStatus.equals(newStatus)) {
                continue;
            }

            if (newStatus.equals(ApplicationStatus.INTERVIEW_SCHEDULED)) {
                if (dto.getInterviewDate() == null) {
                    throw new BusinessException("Interview date and time required for scheduling interview");
                }
                application.setInterviewDate(dto.getInterviewDate());
            }

            application.setStatus(newStatus);
            if (dto.getRecruiterNotes() != null) {
                application.setRecruiterNotes(dto.getRecruiterNotes());
            }
        }
        jobApplicationRepository.saveAll(applications);
    }

    @Transactional
    public boolean deletePost(String recruiterEmail, Long postId) {
        Recruiter recruiter = recruiterRepository.findByUser_Email(recruiterEmail)
                .orElseThrow(() -> new BadRequestException("You are not allowed to delete jobs."));

        JobPost jobPost = jobPostRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("Job post not found"));

        if (!jobPost.getRecruiter().getId().equals(recruiter.getId())) {
            throw new BadRequestException("You are not allowed to delete this job post.");
        }

        // Sirf Soft Delete - Hard delete code hta diya gaya hai
        jobPost.setStatus(JobStatus.DELETED);
        jobPost.setActive(false);
        jobPostRepository.save(jobPost);

        return true;
    }

    public Page<JobListDTO> searchJobs(JobSearchFilterDTO filter) {
        Sort sort = Sort.by(Sort.Direction.fromString(filter.getSortDir()), filter.getSortBy());
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        Page<JobPost> jobs = jobPostRepository.findAll(JobSpecification.searchJobs(filter), pageable);
        return jobs.map(job -> modelMapper.map(job, JobListDTO.class));
    }
}