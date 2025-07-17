package com.jobPortal.Service;

import com.jobPortal.DTO.JobStatusUpdateRequest;
import com.jobPortal.DTO.JobSummaryDTO;
import com.jobPortal.DTO.MultiUseDTO.ApplyJobDTO;
import com.jobPortal.DTO.RecruiterDTO.JobPostRequestDTO;
import com.jobPortal.Enums.ApplicationStatus;
import com.jobPortal.Enums.JobStatus;
import com.jobPortal.Model.*;
import com.jobPortal.Model.Users.Recruiter;
import com.jobPortal.Model.Users.Student;
import com.jobPortal.Model.Users.User;
import com.jobPortal.Repositorie.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service @Slf4j
public class JobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final RecruiterRepository recruiterRepository;
    private final SkillRepository skillRepository;
    private final ApplicationRepository applicationRepository;
    @Autowired
    private ModelMapper modelMapper;


    public JobService(JobRepository jobRepository, UserRepository userRepository, StudentRepository studentRepository, RecruiterRepository recruiterRepository, SkillRepository skillRepository, ApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.recruiterRepository = recruiterRepository;
        this.skillRepository = skillRepository;
        this.applicationRepository = applicationRepository;
    }
    @Transactional
    public ResponseEntity<?> newPost(String username, JobPostRequestDTO postRequestDTO) {
        try{
            ValidatedRecruiter validated = validateUserAndRecruiter(username);
            Recruiter recruiter = validated.recruiter();
            JobPost newPost = modelMapper.map(postRequestDTO,JobPost.class);
            newPost.setPostedDate(new Date());
            if (postRequestDTO.getSkills() != null && !postRequestDTO.getSkills().isEmpty()) {
                List<Skill> skills = skillRepository.findAllByIdIn(postRequestDTO.getSkills());
                newPost.setRequiredSkills(skills);
            }
            newPost.setRecruiter(recruiter);
            recruiter.getJobPosts().add(newPost);
            jobRepository.save(newPost);
            recruiterRepository.save(recruiter);
            return new ResponseEntity<>("Post Successful", HttpStatus.OK);
        }catch (Exception e){
            log.error("Error posting new Job {}",e.getMessage());
            return new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<String> applyJob(String username, ApplyJobDTO jobDTO) {
        try{
            User user = userRepository.findByEmail(username);
            if(user == null) return new ResponseEntity<>("User Not found",HttpStatus.NOT_FOUND);
            Student student = studentRepository.findByUser(user);
            if (student == null) return new ResponseEntity<>("User Not found",HttpStatus.NOT_FOUND);
            JobPost jobPost = jobRepository.findById(jobDTO.getJobId()).orElse(null);
            if(jobPost == null) return new ResponseEntity<>("Job not found",HttpStatus.NOT_FOUND);
            if (jobPost.getStatus().equals(JobStatus.CLOSED) || jobPost.getStatus().equals(JobStatus.EXPIRED) || jobPost.getStatus().equals(JobStatus.DELETED)){
                return new ResponseEntity<>("Job expired or closed",HttpStatus.BAD_REQUEST);
            }
            boolean alreadyApplied = applicationRepository.existsByStudentAndJobPost(student, jobPost);
            if (alreadyApplied) {
                return new ResponseEntity<>("You have already applied for this job", HttpStatus.BAD_REQUEST);
            }
            JobApplication application = new JobApplication();
            application.setStudent(student);
            application.setJobPost(jobPost);
            application.setAppliedDate(new Date());
            application.setStatus(ApplicationStatus.PENDING);
            application.setCoverLetter(jobDTO.getCoverLetter());
            application.setResumeUrl(jobDTO.getResumeUrl());

            student.getApplications().add(application);
            jobPost.getApplications().add(application);

            applicationRepository.save(application);
            studentRepository.save(student);
            jobRepository.save(jobPost);

            return new ResponseEntity<>("Your Job Applied",HttpStatus.OK);
        }catch (Exception e){
         log.error("Error While student applying Job {}",e.getMessage());
         return new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getAllJobs(String keyword, String location, int page, int size) {
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(page, size);
            var jobs = jobRepository.findAll(pageable);
            List<JobPost> jobList = jobs.getContent();
            List<JobSummaryDTO> summaryDTOS = new ArrayList<>();

            for(JobPost job:jobList){
                JobSummaryDTO summaryDTO = new JobSummaryDTO();
                summaryDTO.setId(job.getId());
                summaryDTO.setTitle(job.getTitle());
                summaryDTO.setSalary(job.getSalary());
                summaryDTO.setJobDescription(job.getDescription());
                summaryDTO.setType(job.getType());
                summaryDTO.setLocation(job.getLocation());
                summaryDTO.setCompanyLogoUrl(job.getCompanyLogoUrl());
                summaryDTO.setSalary(job.getSalary());
                summaryDTO.setCompanyName(job.getCompanyName());
                summaryDTO.setExperienceRequired(job.getExperienceRequired());
                summaryDTO.setRequiredSkills(
                        job.getRequiredSkills().stream()
                                .map(Skill::getName)
                                .toList()
                );
                summaryDTO.setPostedDate(job.getPostedDate());
                summaryDTO.setExpiryDate(job.getLastDateToApply());
                summaryDTO.setJobStatus(job.getStatus());
                summaryDTOS.add(summaryDTO);
            }
            return ResponseEntity.ok(summaryDTOS);
        } catch (Exception e) {
            log.error("Error fetching all jobs {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    public ResponseEntity<?> getJobById(Long jobId) {
        JobPost job = jobRepository.findById(jobId).orElse(null);
        if (job == null) return new ResponseEntity<>("Job not found",HttpStatus.NOT_FOUND);
        JobSummaryDTO summaryDTO = new JobSummaryDTO();
        summaryDTO.setId(job.getId());
        summaryDTO.setTitle(job.getTitle());
        summaryDTO.setSalary(job.getSalary());
        summaryDTO.setJobDescription(job.getDescription());
        summaryDTO.setType(job.getType());
        summaryDTO.setLocation(job.getLocation());
        summaryDTO.setCompanyLogoUrl(job.getCompanyLogoUrl());
        summaryDTO.setSalary(job.getSalary());
        summaryDTO.setCompanyName(job.getCompanyName());
        summaryDTO.setExperienceRequired(job.getExperienceRequired());
        summaryDTO.setRequiredSkills(
                job.getRequiredSkills().stream()
                        .map(Skill::getName)
                        .toList()
        );
        summaryDTO.setPostedDate(job.getPostedDate());
        summaryDTO.setExpiryDate(job.getLastDateToApply());
        summaryDTO.setJobStatus(job.getStatus());
        return ResponseEntity.ok(summaryDTO);
    }


    @Transactional
    public ResponseEntity<?> updateJob(String recruiterEmail, Long jobId, JobPostRequestDTO jobPostDTO) {
        try {
            var recruiter = validateUserAndRecruiter(recruiterEmail).recruiter();
            var jobOpt = jobRepository.findByIdAndRecruiter_Id(jobId, recruiter.getId());

            if (jobOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found");

            JobPost job = jobOpt.get();

            // Verify the recruiter owns this job post
            if (!job.getRecruiter().getId().equals(recruiter.getId()))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized to update this job");

            // Update fields
            modelMapper.map(jobPostDTO, job);
            if (jobPostDTO.getSkills() != null && !jobPostDTO.getSkills().isEmpty()) {
                List<Skill> skills = skillRepository.findAllByIdIn(jobPostDTO.getSkills());
                job.setRequiredSkills(skills);
            }

            jobRepository.save(job);
            return ResponseEntity.ok("Job updated successfully");
        } catch (Exception e) {
            log.error("Error updating job: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @Transactional
    public ResponseEntity<?> deleteJob(String recruiterEmail, Long jobId) {
        try {
            var recruiter = validateUserAndRecruiter(recruiterEmail).recruiter();
            var jobOpt = jobRepository.findByIdAndRecruiter_Id(jobId, recruiter.getId());

            if (jobOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found");

            JobPost job = jobOpt.get();
            if (!job.getRecruiter().getId().equals(recruiter.getId()))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized to delete this job");

            jobRepository.delete(job);
            return ResponseEntity.ok("Job deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting job: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @Transactional
    public ResponseEntity<?> updateJobStatus(String recruiterEmail, JobStatusUpdateRequest request) {
        try {
            var recruiter = validateUserAndRecruiter(recruiterEmail).recruiter();
            var jobOpt = jobRepository.findByIdAndRecruiter_Id(request.getJobId(), recruiter.getId());

            if (jobOpt.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found");

            if (jobOpt.get().getStatus().equals(JobStatus.DELETED) || jobOpt.get().getStatus().equals(JobStatus.EXPIRED))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found");

            JobPost job = jobOpt.get();
            job.setStatus(request.getJobStatus());
            jobRepository.save(job);

            return ResponseEntity.ok("Job status updated to " + request.getJobStatus());
        } catch (Exception e) {
            log.error("Error updating job status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }
    private ValidatedRecruiter validateUserAndRecruiter(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new RuntimeException("User not found");
        Recruiter recruiter = recruiterRepository.findByUser(user);
        if (recruiter == null) throw new RuntimeException("Recruiter not found");
        return new ValidatedRecruiter(user, recruiter);
    }
}

