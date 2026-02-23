package com.jobPortal.Service;

import com.jobPortal.DTO.JobListDTO;
import com.jobPortal.DTO.JobPostDTO;
import com.jobPortal.DTO.JobRequestDTO;
import com.jobPortal.DTO.JobSearchFilterDTO;
import com.jobPortal.DTO.MultiUseDTO.SkillDTO;
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
    private final  ModelMapper modelMapper;
    private final JobApplicationRepository jobApplicationRepository;
    private final SkillRepository skillRepository;


    /**
     * Post a new job for a recruiter
     */
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

        // Map DTO to Entity
        JobPost jobPost = modelMapper.map(jobPostDTO, JobPost.class);

        // Basic defaults
        jobPost.setRecruiter(recruiter);
        jobPost.setViewCount(0);
        jobPost.setApplicationsCount(0);
        jobPost.setActive(true);
        jobPost.setPostedDate(LocalDateTime.now());
        if (jobPost.getStatus() == null) {
            jobPost.setStatus(JobStatus.OPEN);
        }

        // ================= SKILL UPSERT LOGIC =================
        List<Skill> managedSkills = new ArrayList<>();

        if (jobPostDTO.getRequiredSkills() != null) {
            for (SkillDTO skillDto : jobPostDTO.getRequiredSkills()) {
                if (skillDto.getName() == null || skillDto.getName().trim().isEmpty()) {
                    continue;
                }
                String normalizedName = skillDto.getName().trim().toLowerCase();

                // Check in DB: Find existing or create new
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
        // ======================================================

        JobPost savedJob = jobPostRepository.save(jobPost);
        recruiter.getJobPosts().add(savedJob);
        recruiter.setTotalJobsPosted(recruiter.getJobPosts().size());

        return true;
    }

    @Transactional
    public boolean updatePost(String recruiterEmail,
                              Long postId,
                              JobPostDTO dto) {

        // 1️⃣ Verify recruiter
        Recruiter recruiter = recruiterRepository.findByUser_Email(recruiterEmail)
                .orElseThrow(() ->
                        new BadRequestException("You are not allowed to update jobs."));

        // 2️⃣ Fetch job
        JobPost jobPost = jobPostRepository.findById(postId)
                .orElseThrow(() ->
                        new BusinessException("Job post not found"));

        // 3️⃣ Ownership check
        if (!jobPost.getRecruiter().getId().equals(recruiter.getId())) {
            throw new BadRequestException("You are not allowed to update this job.");
        }

        // 4️⃣ Salary validation
        if (dto.getMinSalary() != null && dto.getMaxSalary() != null
                && dto.getMaxSalary() < dto.getMinSalary()) {
            throw new BusinessException("Max salary cannot be less than min salary.");
        }

        // 5️⃣ Last date validation
        if (dto.getLastDateToApply() != null &&
                dto.getLastDateToApply().isBefore(LocalDate.now())) {
            throw new BusinessException("Last date cannot be in the past.");
        }

        // 6️⃣ Update ONLY IMPORTANT FIELDS

        jobPost.setTitle(dto.getTitle());
        jobPost.setDescription(dto.getDescription());
        jobPost.setLocation(dto.getLocation());
        jobPost.setType(dto.getType());
        jobPost.setCategory(dto.getCategory());
        jobPost.setMinSalary(dto.getMinSalary());
        jobPost.setMaxSalary(dto.getMaxSalary());
        jobPost.setExperienceRequired(dto.getExperienceRequired());
        jobPost.setLastDateToApply(dto.getLastDateToApply());

        // Skills update
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



    /**
     * Get all jobs posted by a recruiter
     */
    public Page<JobListDTO> getJobsByRecruiterUserId(
            String email,
            int page,
            int size
    ) {

        recruiterRepository.findByUser_Email(email)
                .orElseThrow(() ->
                        new BadRequestException("You are not allowed to view jobs."));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "postedDate")
        );

        Page<JobPost> jobs = jobPostRepository.findAll(
                JobSpecification.recruiterJobs(email),
                pageable
        );

        return jobs.map(job -> modelMapper.map(job, JobListDTO.class));
    }

    /**
     * Get all applications for a specific job by recruiter
     */
    @Transactional
    public Page<JobApplicationListDTO> getApplicationsByJobId(
            UUID userId,
            Long jobId,
            int page,
            int size
    ) {

        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new BadRequestException("Applications not found for this job"));

        JobPost jobPost = jobPostRepository.findById(jobId)
                .orElseThrow(() ->
                        new BusinessException("Job not found"));

        if (!jobPost.getRecruiter().getId().equals(recruiter.getId())) {
            throw new BadRequestException("Application not found for this job");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "appliedAt")
        );

        Page<JobApplication> applications = jobApplicationRepository.findByJobPost_Id(jobId, pageable);

// Summary mapping
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

        // Step 1: Verify recruiter
        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("You are not allowed to view applications"));

        // Step 2: Fetch application from JobApplicationRepository
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException("Application not found"));

        // Step 3: Verify recruiter owns this job post
        JobPost jobPost = application.getJobPost();
        if (!jobPost.getRecruiter().getId().equals(recruiter.getId())) {
            throw new BadRequestException("You are not allowed to view this application");
        }

        // Step 4: Map to DTO
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


    // ===== Public Operations =====

    /** Get job by ID */
    public JobRequestDTO getJobById(Long jobId) {

        Optional<JobPost> jobOpt = jobPostRepository.findOne(
                JobSpecification.publicVisible()
                        .and((root, query, cb) ->
                                cb.equal(root.get("id"), jobId))
        );
        if (jobOpt.isEmpty()) {
            return null;
        }
        JobPost job = jobOpt.get();

        // ===== Increment view count =====
        job.setViewCount(job.getViewCount() + 1);
        jobPostRepository.save(job);

        // ===== Map to DTO =====
        return modelMapper.map(job, JobRequestDTO.class);
    }

    /** Popular jobs (by views or applications count) */
    public Page<JobListDTO> getPopularJobs(int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "applicationsCount")
        );

        Page<JobPost> jobs = jobPostRepository.findAll(
                JobSpecification.publicVisible(),
                pageable
        );

        return jobs.map(job -> modelMapper.map(job, JobListDTO.class));
    }

    /** Jobs by category */
    public List<JobListDTO> findJobsByCategory(String category) {

        var spec = JobSpecification.publicVisible()
                .and((root, query, cb) ->
                        cb.equal(
                                cb.lower(root.get("category")),
                                category.toLowerCase()
                        )
                );

        List<JobPost> jobs = jobPostRepository.findAll(spec);

        return jobs.stream()
                .map(job -> modelMapper.map(job, JobListDTO.class))
                .toList();
    }

    @Transactional
    public void updateApplicationStatus(UUID recruiterId, Long applicationId, UpdateApplicationStatusDTO dto) {

        // 1. Verify recruiter
        Recruiter recruiter = recruiterRepository.findByUserId(recruiterId)
                .orElseThrow(() -> new BadRequestException("You are not allowed to update applications"));

        // 2. Fetch application
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException("Application not found"));

        // 3. Check recruiter owns this job
        if (!application.getJobPost().getRecruiter().getId().equals(recruiter.getId())) {
            throw new BadRequestException("You are not allowed to update this application");
        }

        // 4. Ensure forward-only progression
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
        }

        // 5. Update status and optional notes
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

            // Check recruiter owns the job
            if (!application.getJobPost().getRecruiter().getId().equals(recruiter.getId())) {
                continue; // skip applications not owned by this recruiter
            }

            // Forward-only progression
            ApplicationStatus currentStatus = application.getStatus();
            ApplicationStatus newStatus = dto.getStatus();

            if (!(ApplicationHelper.canMoveForward(currentStatus, newStatus))) {
                continue; // skip invalid status updates
            }

            if (currentStatus.equals(newStatus)) {
                continue; // skip if already in desired status
            }

            // Interview date logic
            if (newStatus.equals(ApplicationStatus.INTERVIEW_SCHEDULED)) {
                if (dto.getInterviewDate() == null) {
                    throw new BusinessException("Interview date and time required for scheduling interview");
                }
                application.setInterviewDate(dto.getInterviewDate());
            }

            // Update status and optional notes
            application.setStatus(newStatus);
            if (dto.getRecruiterNotes() != null) {
                application.setRecruiterNotes(dto.getRecruiterNotes());
            }
        }

        jobApplicationRepository.saveAll(applications);
    }

    @Transactional
    public boolean deletePost(String recruiterEmail, Long postId) {

        // 1. Verify recruiter exists
        Recruiter recruiter = recruiterRepository.findByUser_Email(recruiterEmail)
                .orElseThrow(() -> new BadRequestException("You are not allowed to delete jobs."));

        // 2. Fetch the job post
        JobPost jobPost = jobPostRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("Job post not found"));

        // 3. Verify recruiter owns this job
        if (!jobPost.getRecruiter().getId().equals(recruiter.getId())) {
            throw new BadRequestException("You are not allowed to delete this job post.");
        }

        // 4. Soft delete
        // Soft delete: mark as closed and inactive instead of deleting from DB
        jobPost.setStatus(JobStatus.CLOSED);
        jobPost.setActive(false);
        jobPostRepository.save(jobPost);

        // Optional: remove from recruiter's job list
        recruiter.getJobPosts().removeIf(j -> j.getId().equals(postId));
        recruiter.setTotalJobsPosted(recruiter.getJobPosts().size());

        return true;
    }

    public Page<JobListDTO> searchJobs(JobSearchFilterDTO filter) {
        Sort sort = Sort.by(
                Sort.Direction.fromString(filter.getSortDir()),
                filter.getSortBy()
        );

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                sort
        );

        Page<JobPost> jobs = jobPostRepository.findAll(
                JobSpecification.searchJobs(filter),
                pageable
        );

        return jobs.map(job -> modelMapper.map(job, JobListDTO.class));
    }

}