package com.jobPortal.Service;

import com.jobPortal.DTO.RecruiterDTO.JobApplicationRecruiterViewDTO;
import com.jobPortal.Model.JobApplication;
import com.jobPortal.Model.JobPost;
import com.jobPortal.Model.Skill;
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

import java.util.List;

@Service
@Slf4j
public class ApplicationService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final RecruiterRepository recruiterRepository;
    private final ApplicationRepository applicationRepository;
    @Autowired
    private ModelMapper modelMapper;

    public ApplicationService(JobRepository jobRepository, UserRepository userRepository, StudentRepository studentRepository, RecruiterRepository recruiterRepository, ApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.recruiterRepository = recruiterRepository;
        this.applicationRepository = applicationRepository;
    }


    public ResponseEntity<?> getApplicantsForJob(String recruiterEmail, Long jobId) {
        try {
            var recruiter = validateUserAndRecruiter(recruiterEmail).recruiter();
            var jobOpt = jobRepository.findByIdAndRecruiter_Id(jobId, recruiter.getId());

            if (jobOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found");

            JobPost job = jobOpt.get();
            if (!job.getRecruiter().getId().equals(recruiter.getId()))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");

            List<JobApplication> applications = job.getApplications();

            List<JobApplicationRecruiterViewDTO> dtoList = applications.stream().map(app -> {
                Student student = app.getStudent();

                JobApplicationRecruiterViewDTO dto = new JobApplicationRecruiterViewDTO();
                dto.setApplicationId(app.getId());
                dto.setJobPostId(String.valueOf(job.getId()));
                dto.setJobPostName(job.getTitle());
                dto.setStudentName(student.getUser().getFullName());
                dto.setStudentEmail(student.getUser().getEmail());
                dto.setSkills(student.getSkills().stream().map(Skill::getName).toList());
                dto.setResumeUrl(app.getResumeUrl());
                dto.setLinkedinUrl(student.getLinkedinProfile());
                dto.setGithubUrl(student.getGithubProfile());
                dto.setAppliedDate(app.getAppliedDate());
                dto.setStatus(app.getStatus());
                dto.setSelection(app.isSelection());
                dto.setCoverLetter(app.getCoverLetter());
                return dto;
            }).toList();
            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            log.error("Error getting applicants: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    public ResponseEntity<?> getApplicantById(String recruiterEmail, Long applicationId) {
        try {
            var recruiter = validateUserAndRecruiter(recruiterEmail).recruiter();
            var appOpt = applicationRepository.findByIdAndJobPost_Recruiter_Id(applicationId, recruiter.getId());

            if (appOpt.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Application not found");

            JobApplication app = appOpt.get();
            JobPost job = app.getJobPost();
            Student student = app.getStudent();

            JobApplicationRecruiterViewDTO dto = new JobApplicationRecruiterViewDTO();
            dto.setApplicationId(app.getId());
            dto.setJobPostId(String.valueOf(job.getId()));
            dto.setJobPostName(job.getTitle());
            dto.setStudentName(student.getUser().getFullName());
            dto.setStudentEmail(student.getUser().getEmail());
            dto.setSkills(student.getSkills().stream().map(Skill::getName).toList());
            dto.setResumeUrl(app.getResumeUrl());
            dto.setLinkedinUrl(student.getLinkedinProfile());
            dto.setGithubUrl(student.getGithubProfile());
            dto.setAppliedDate(app.getAppliedDate());
            dto.setStatus(app.getStatus());
            dto.setSelection(app.isSelection());
            dto.setCoverLetter(app.getCoverLetter());
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            log.error("Error fetching application details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @Transactional
    public ResponseEntity<String> updateApplicationSelection(String recruiterEmail, Long applicationId, boolean selection) {
        try {
            var recruiter = validateUserAndRecruiter(recruiterEmail).recruiter();

            var appOpt = applicationRepository.findByIdAndJobPost_Recruiter_Id(applicationId, recruiter.getId());
            if (appOpt.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Application not found or unauthorized access");

            JobApplication application = appOpt.get();
            application.setSelection(selection);

            applicationRepository.save(application);
            return ResponseEntity.ok("Selection status updated to " + selection);
        } catch (Exception e) {
            log.error("Error updating selection status: {}", e.getMessage());
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
