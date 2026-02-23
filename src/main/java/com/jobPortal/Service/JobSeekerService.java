package com.jobPortal.Service;

import com.jobPortal.DTO.JobSeekerDTO.*;
import com.jobPortal.DTO.MultiUseDTO.SkillDTO;
import com.jobPortal.Exception.BusinessException;
import com.jobPortal.Model.JobApplication;
import com.jobPortal.Model.JobPost;
import com.jobPortal.Model.Seeker.Certification;
import com.jobPortal.Model.Seeker.Education;
import com.jobPortal.Model.Seeker.Experience;
import com.jobPortal.Model.Skill;
import com.jobPortal.Model.Users.Seeker;
import com.jobPortal.Model.Users.User;
import com.jobPortal.Repository.*;
import com.jobPortal.Util.CandidateHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobSeekerService {

    private final SeekerRepository seekerRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final CertificationRepository certificationRepository;
    private final SkillRepository skillRepository;
    private final ModelMapper modelMapper;

    @Value("${file.upload-dir.resumes:uploads/resumes}")
    private String resumeUploadDir;

    // ================= QUICK JOB APPLY =================
    @Transactional
    public boolean quickJobApply(Long jobId, UUID userId) {
        Seeker seeker = getSeeker(userId);

        JobPost jobPost = jobRepository.findById(jobId)
                .orElseThrow(() -> new BusinessException("Job not found"));

        if (!seeker.isProfileComplete())
            throw new BusinessException("Please complete your profile first");

        if (jobApplicationRepository.existsBySeekerAndJobPost(seeker, jobPost))
            throw new BusinessException("You have already applied for this job.");

        JobApplication jobApplication = new JobApplication();
        jobApplication.setJobPost(jobPost);
        jobApplication.setSeeker(seeker);
        jobApplication.setResumeUrl(seeker.getResumeUrl());
        jobApplication.setAiMatchScore(80);
        jobPost.setApplicationsCount(jobPost.getApplicationsCount() + 1);

        jobApplicationRepository.save(jobApplication);
        jobRepository.save(jobPost);
        return true;
    }

    // ================= UPDATE PERSONAL & PROFESSIONAL DETAILS =================
    @Transactional
    public boolean updatePersonalDetails(PersonalDetailDTO dto, UUID userId) {
        Seeker seeker = getSeeker(userId);

        if (dto.getDob() != null) {
            int age = Period.between(dto.getDob(), LocalDate.now()).getYears();
            if (age < 18) throw new BusinessException("You must be at least 18 years old.");
        }

        modelMapper.map(dto, seeker);
        seekerRepository.save(seeker);
        return true;
    }
    // ================= UPDATE PROFESSIONAL DETAILS =================
    @Transactional
    public boolean updateProfessionalDetails(ProfessionalDetailsDTO dto, UUID userId) {
        Seeker seeker = getSeeker(userId);
        List<SkillDTO> incomingSkills = dto.getSkills();
        modelMapper.map(dto, seeker);
        if (incomingSkills != null) {
            List<Skill> managedSkills = new ArrayList<>();

            for (SkillDTO skillDto : incomingSkills) {
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
            seeker.setSkills(managedSkills);
        }
        seekerRepository.save(seeker);
        return true;
    }

    // ================= EDUCATION =================
    @Transactional
    public boolean addEducation(EducationDTO dto, UUID userId) {
        Seeker seeker = getSeeker(userId);
        Education education = modelMapper.map(dto, Education.class);
        education.setSeeker(seeker);
        educationRepository.save(education);
        return true; // Bass true return kar rahe hain, entity nahi
    }

    @Transactional
    public boolean updateEducation(Long educationId, EducationDTO dto, UUID userId) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new BusinessException("Education entry not found"));
        validateOwnership(education.getSeeker().getId(), userId);

        modelMapper.map(dto, education);
        educationRepository.save(education);
        return true;
    }

    @Transactional
    public boolean deleteEducation(Long educationId, UUID userId) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new BusinessException("Education entry not found"));
        validateOwnership(education.getSeeker().getId(), userId);

        educationRepository.delete(education);
        return true;
    }

    // ================= EXPERIENCE =================
    @Transactional
    public boolean addExperience(ExperienceDTO dto, UUID userId) {
        Seeker seeker = getSeeker(userId);
        Experience experience = modelMapper.map(dto, Experience.class);
        experience.setSeeker(seeker);
        experienceRepository.save(experience);
        return true; // Bass true return kar rahe hain
    }

    @Transactional
    public boolean updateExperience(Long experienceId, ExperienceDTO dto, UUID userId) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new BusinessException("Experience entry not found"));
        validateOwnership(experience.getSeeker().getId(), userId);

        modelMapper.map(dto, experience);
        experienceRepository.save(experience);
        return true;
    }

    @Transactional
    public boolean deleteExperience(Long experienceId, UUID userId) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new BusinessException("Experience entry not found"));
        validateOwnership(experience.getSeeker().getId(), userId);

        experienceRepository.delete(experience);
        return true;
    }

    // ================= CERTIFICATION =================
    @Transactional
    public boolean addCertification(CertificationDTO dto, UUID userId) {
        Seeker seeker = getSeeker(userId);
        Certification certification = modelMapper.map(dto, Certification.class);
        certification.setSeeker(seeker);
        certificationRepository.save(certification);
        return true;
    }

    @Transactional
    public boolean deleteCertification(Long certificationId, UUID userId) {
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new BusinessException("Certification not found"));
        validateOwnership(certification.getSeeker().getId(), userId);

        certificationRepository.delete(certification);
        return true;
    }

    // ================= UPLOAD DOCUMENTS =================
    @Transactional
    public boolean uploadDocuments(MultipartFile resume, MultipartFile profileImage, UUID userId) {
        Seeker seeker = getSeeker(userId);

        try {
            if (resume != null && !resume.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + resume.getOriginalFilename();
                Path uploadPath = Paths.get(resumeUploadDir);

                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

                Files.copy(resume.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                seeker.setResumeUrl("/" + resumeUploadDir + "/" + fileName);
            }

            seeker.setProfileComplete(true);
            seekerRepository.save(seeker);
            return true;
        } catch (IOException e) {
            log.error("File upload failed for user {}: {}", userId, e.getMessage());
            throw new BusinessException("Failed to upload document. Please try again.");
        }
    }

    // ================= GETTERS (Read Only - Exposing ONLY Responses) =================

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<EducationResponse> getEducations(UUID userId) {
        return educationRepository.findBySeeker_Id(userId)
                .stream()
                .map(edu -> new EducationResponse(
                        edu.getId(),
                        edu.getDegree(),
                        edu.getFieldOfStudy(),
                        edu.getCollegeName(),
                        edu.getCountry(),
                        edu.getStartYear(),
                        edu.getEndYear(),
                        edu.getGradeType(),
                        edu.getGradeValue()
                ))
                .collect(Collectors.toList());
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<ExperienceResponse> getExperiences(UUID userId) {
        return experienceRepository.findBySeeker_Id(userId)
                .stream()
                .map(exp -> new ExperienceResponse(
                        exp.getId(),
                        exp.getJobTitle(),
                        exp.getCompanyName(),
                        exp.getStartDate(),
                        exp.getEndDate(),
                        exp.getLocation(),
                        exp.getDescription()
                ))
                .collect(Collectors.toList());
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<CertificationResponse> getCertifications(UUID userId) {
        return certificationRepository.findBySeeker_Id(userId)
                .stream()
                .map(cert -> new CertificationResponse(
                        cert.getId(),
                        cert.getName(),
                        cert.getIssuingOrganization(),
                        cert.getIssueDate(),
                        cert.getExpiryDate(),
                        cert.getCredentialUrl() // Bug fixed here (pehle do baar pass ho raha tha)
                ))
                .collect(Collectors.toList());
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PersonalDetailDTO getPersonalDetails(UUID userId) {
        return modelMapper.map(getSeeker(userId), PersonalDetailDTO.class);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ProfessionalDetailsDTO getProfessionalDetails(UUID userId) {
        return modelMapper.map(getSeeker(userId), ProfessionalDetailsDTO.class);
    }

    // ================= REMOVE SKILL (Unlink) =================
    @Transactional
    public boolean removeSkill(Long skillId, UUID userId) {
        Seeker seeker = getSeeker(userId);

        boolean isRemoved = seeker.getSkills().removeIf(skill -> skill.getId().equals(skillId));

        if (!isRemoved) {
            throw new BusinessException("Ye skill aapki profile mein nahi mili.");
        }
        seekerRepository.save(seeker);

        return true;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public SeekerFullProfileDTO getFullProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));
        Seeker seeker = getSeeker(userId);

        SeekerFullProfileDTO dto = modelMapper.map(seeker, SeekerFullProfileDTO.class);

        // User fields mapping
        dto.setUserId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setActive(user.isActive());
        dto.setVerified(user.isVerified());
        dto.setProfileUrl(user.getProfileUrl());

        // 🚨 Explicit Mapping for Lists to avoid ModelMapper null/binding issues
        if (seeker.getExperienceList() != null) {
            dto.setExperienceList(seeker.getExperienceList().stream()
                    .map(exp -> modelMapper.map(exp, ExperienceResponse.class))
                    .collect(Collectors.toList()));
        }

        if (seeker.getEducationList() != null) {
            dto.setEducationList(seeker.getEducationList().stream()
                    .map(edu -> modelMapper.map(edu, EducationResponse.class))
                    .collect(Collectors.toList()));
        }

        if (seeker.getCertifications() != null) {
            dto.setCertifications(seeker.getCertifications().stream()
                    .map(cert -> modelMapper.map(cert, CertificationResponse.class))
                    .collect(Collectors.toList()));
        }

        if (seeker.getSkills() != null) {
            dto.setSkills(seeker.getSkills().stream()
                    .map(skill -> new SkillResponse(skill.getId(), skill.getName()))
                    .collect(Collectors.toList()));
        }

        // Completion Logic
        int score = CandidateHelper.calculateProfileCompletion(seeker);
        dto.setProfileCompletion(score);
        dto.setProfileComplete(score >= 80);

        return dto;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<JobApplicationListDTO> getJobApplicationsByJobSeekerId(UUID userId) {
        return jobApplicationRepository.findBySeeker_Id(userId)
                .stream()
                .map(app -> new JobApplicationListDTO(
                        app.getId(),
                        app.getJobPost() != null ? app.getJobPost().getTitle() : "N/A",
                        app.getJobPost() != null ? app.getJobPost().getCompanyName() : "N/A",
                        app.getStatus(),
                        app.getAppliedAt() != null ? app.getAppliedAt() : LocalDateTime.now(),
                        app.getAiMatchScore() != null ? app.getAiMatchScore() : 0
                ))
                .collect(Collectors.toList());
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public JobApplicationDTO getApplicationById(Long id, UUID userId) {
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Application not found"));
        validateOwnership(application.getSeeker().getId(), userId);

        JobApplicationDTO dto = new JobApplicationDTO();
        dto.setApplicationId(application.getId());
        dto.setJobTitle(application.getJobPost().getTitle());
        dto.setCompanyName(application.getJobPost().getCompanyName());
        dto.setStatus(application.getStatus());
        dto.setAppliedAt(application.getAppliedAt());
        dto.setAiMatchScore(application.getAiMatchScore());
        dto.setResumeUrl(application.getResumeUrl());
        return dto;
    }

    // ================= PRIVATE HELPERS =================
    private Seeker getSeeker(UUID userId) {
        return seekerRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    private void validateOwnership(UUID ownerId, UUID userId) {
        if (!ownerId.equals(userId)) throw new BusinessException("Unauthorized access");
    }
}