package com.jobPortal.Service;

import com.jobPortal.DTO.JobPostDTO;
import com.jobPortal.DTO.JobRequestDTO;
import com.jobPortal.Enums.JobStatus;
import com.jobPortal.Model.JobPost;
import com.jobPortal.Model.Skill;
import com.jobPortal.Model.Users.Recruiter;
import com.jobPortal.Repositorie.JobRepository;
import com.jobPortal.Repositorie.RecruiterRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    @Autowired
    private RecruiterRepository recruiterRepository;

    @Autowired
    private JobRepository jobPostRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Post a new job for a recruiter
     *
     * @param email       The email of the recruiter (username)
     * @param jobPostDTO  Job data from client
     * @return ResponseEntity with job info or error message
     */
    public ResponseEntity<?> postJob(String email, JobPostDTO jobPostDTO) {

        //  Fetch recruiter with active user check
        Optional<Recruiter> recruiterOpt = recruiterRepository.findByUser_Email(email);
        if (recruiterOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not allowed to post jobs. Contact admin if you think this is a mistake.");
        }
        Recruiter recruiter = recruiterOpt.get();

        //  Validate salary range
        if (jobPostDTO.getMinSalary() != null && jobPostDTO.getMaxSalary() != null &&
                jobPostDTO.getMaxSalary() < jobPostDTO.getMinSalary()) {
            return ResponseEntity.badRequest().body("Max salary cannot be less than min salary.");
        }

        //  Validate last date to apply
        if (jobPostDTO.getLastDateToApply() != null &&
                jobPostDTO.getLastDateToApply().isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest().body("Last date to apply cannot be in the past.");
        }

        //  Map DTO to Entity using ModelMapper
        JobPost jobPost = modelMapper.map(jobPostDTO, JobPost.class);

        //  Set recruiter reference
        jobPost.setRecruiter(recruiter);

        //  Initialize analytics and defaults
        jobPost.setViewCount(0);
        jobPost.setApplicationsCount(0);
        jobPost.setActive(true);
        if (jobPost.getStatus() == null) {
            jobPost.setStatus(com.jobPortal.Enums.JobStatus.OPEN);
        }

        //  Validate requiredSkills list (optional)
        if (jobPost.getRequiredSkills() == null) {
            jobPost.setRequiredSkills(new ArrayList<>());
        } else {
            List<Skill> skills = new ArrayList<>();
            jobPost.getRequiredSkills().forEach(skill -> {
                if (skill.getName() != null && !skill.getName().isEmpty()) {
                    skills.add(skill);
                }
            });
            jobPost.setRequiredSkills(skills);
        }

        // Save job post to DB
        JobPost savedJob = jobPostRepository.save(jobPost);

        // Update recruiter analytics
        recruiter.getJobPosts().add(savedJob);
        recruiter.setTotalJobsPosted(recruiter.getJobPosts().size());

        // Return saved job with OK
        return ResponseEntity.status(HttpStatus.CREATED).body("Job Posted");
    }

    public ResponseEntity<?> getAllPosts(String email) {
        // Fetch recruiter with active user check
        Optional<Recruiter> recruiterOpt = recruiterRepository.findByUser_Email(email);
        if (!recruiterOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not allowed to view jobs. Contact admin if you think this is a mistake.");
        }
        Recruiter recruiter = recruiterOpt.get();

        List<JobPost> allPosts = recruiter.getJobPosts();

        // Map each JobPost to JobRequestDTO
        List<JobRequestDTO> posts = new ArrayList<>();
        for (JobPost jobPost : allPosts) {
            JobRequestDTO dto = modelMapper.map(jobPost, JobRequestDTO.class);

            // Map required skills names
            if (jobPost.getRequiredSkills() != null) {
                List<String> skillNames = new ArrayList<>();
                jobPost.getRequiredSkills().forEach(skill -> skillNames.add(skill.getName()));
                dto.setRequiredSkills(skillNames);
            } else {
                dto.setRequiredSkills(new ArrayList<>());
            }

            posts.add(dto);
        }

        return ResponseEntity.ok(posts);
    }


    public ResponseEntity<?> getJobById(Long jobId) {
        Optional<JobPost> jobOpt = jobPostRepository.findById(jobId);

        if (jobOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Job not found");
        }

        JobPost job = jobOpt.get();

        // Check if job is active and open
        if (!job.isActive() || job.getStatus() != JobStatus.OPEN) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Job is not available");
        }

        // Map entity → DTO
        JobRequestDTO dto = modelMapper.map(job, JobRequestDTO.class);

        // Map required skills (Skill → String)
        if (job.getRequiredSkills() != null) {
            List<String> skills = new ArrayList<>();
            job.getRequiredSkills().forEach(skill -> skills.add(skill.getName()));
            dto.setRequiredSkills(skills);
        }
        return ResponseEntity.ok(dto);
    }



    public ResponseEntity<?> deletePost(String email, Long postId) {
        Optional<Recruiter> recruiterOpt = recruiterRepository.findByUser_Email(email);
        if (recruiterOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not allowed to delete jobs. Contact admin if you think this is a mistake.");
        }
        Recruiter recruiter = recruiterOpt.get();

        // Fetch job post
        Optional<JobPost> jobOpt = jobPostRepository.findById(postId);
        if (jobOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Job not found");
        }
        JobPost jobPost = jobOpt.get();

        // Check if the recruiter owns this job
        if (!jobPost.getRecruiter().getId().equals(recruiter.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not allowed to delete this job post.");
        }

        // Soft delete: set isActive = false
        jobPost.setActive(false);
        jobPostRepository.save(jobPost);

        // Optionally update recruiter job analytics
        recruiter.getJobPosts().removeIf(j -> j.getId().equals(postId));
        recruiter.setTotalJobsPosted(recruiter.getJobPosts().size());
        recruiterRepository.save(recruiter);

        return ResponseEntity.ok("Job post has been deleted successfully.");
    }


}
