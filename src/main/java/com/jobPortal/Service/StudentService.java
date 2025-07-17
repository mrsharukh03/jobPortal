package com.jobPortal.Service;

import com.jobPortal.DTO.MultiUseDTO.ApplicationDTO;
import com.jobPortal.DTO.StudentDTO.EducationDTO;
import com.jobPortal.DTO.StudentDTO.PersonalDetailDTO;
import com.jobPortal.DTO.StudentDTO.StudentProfileRequestDTO;
import com.jobPortal.Model.*;
import com.jobPortal.Model.Users.Recruiter;
import com.jobPortal.Model.Users.Student;
import com.jobPortal.Model.Users.User;
import com.jobPortal.Repositorie.SkillRepository;
import com.jobPortal.Repositorie.StudentRepository;
import com.jobPortal.Repositorie.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Service @Slf4j
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final ModelMapper modelMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public StudentService(StudentRepository studentRepository, UserRepository userRepository, SkillRepository skillRepository, ModelMapper modelMapper) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.modelMapper = modelMapper;
    }

    public ResponseEntity<?> updatePersonalInfo(PersonalDetailDTO personalDetailDTO, String username) {
        try {
            ValidatedStudent validated = validateUserAndStudent(username);
            User user = validated.user();
            Student student = validated.student();
            // Update only personal fields
            student.setPhone(personalDetailDTO.getPhone());
            student.setGender(personalDetailDTO.getGender());
            student.setDOB(personalDetailDTO.getDOB());
            student.setMarriageStatus(personalDetailDTO.getMarriageStatus());
            studentRepository.save(student);
            return new ResponseEntity<>("Profile updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating profile", e);
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> handleStudentUpload(String linkedin, String github, MultipartFile image, MultipartFile resume, String username){
        try {
            ValidatedStudent validated = validateUserAndStudent(username);
            User user = validated.user();
            Student student = validated.student();

            // Validate files
            if (!isValidImage(image)) {
                throw new IllegalArgumentException("Invalid image file (only JPG/PNG, max 1MB)");
            }
            if (!isValidResume(resume)) {
                throw new IllegalArgumentException("Invalid resume file (only PDF/DOC/DOCX, max 10MB)");
            }

            // Extract extensions
            String imageExtension = getFileExtension(Objects.requireNonNull(image.getOriginalFilename()));
            String resumeExtension = getFileExtension(Objects.requireNonNull(resume.getOriginalFilename()));

            // Create filenames
            String imageName = "image_" + user.getId() + imageExtension;
            String resumeName = "resume_" + student.getId() + resumeExtension;

            // Paths
            Path imagePath = Paths.get(uploadDir, imageName);
            Path resumePath = Paths.get(uploadDir, resumeName);

            // Save files
            Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(resume.getInputStream(), resumePath, StandardCopyOption.REPLACE_EXISTING);

            // Update student entity
            student.setGithubProfile(github);
            student.setLinkedinProfile(linkedin);
            student.setProfileImage(imagePath.toString());
            student.setResumeUrl(resumePath.toString());

            studentRepository.save(student);
            return new ResponseEntity<>("Upload successful",HttpStatus.OK);
        }catch (Exception e){
         log.error("Error uploading user data : {}",e.getMessage());
            return new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper function
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex);
        }
        return "";
    }


    private boolean isValidImage(MultipartFile file) {
        String contentType = file.getContentType();
        return file.getSize() <= 1_000_000 &&
                (contentType.equals("image/jpeg") || contentType.equals("image/png"));
    }

    private boolean isValidResume(MultipartFile file) {
        String contentType = file.getContentType();
        return file.getSize() <= 10_000_000 &&
                (contentType.equals("application/pdf") ||
                        contentType.equals("application/msword") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
    }

    @PostConstruct
    public void createUploadDirIfNotExists() {
        Path path = Paths.get(uploadDir);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                log.error("Failed to create upload directory", e);
            }
        }
    }

    public ResponseEntity<?> getProfile(String username) {
            try {
                ValidatedStudent validated = validateUserAndStudent(username);
                User user = validated.user();
                Student student = validated.student();
                StudentProfileRequestDTO requestDTO = new StudentProfileRequestDTO();
                requestDTO.setName(user.getFullName());
                requestDTO.setEmail(user.getEmail());
                requestDTO.setPhone(student.getPhone());
                requestDTO.setLinkedinProfile(student.getLinkedinProfile());
                requestDTO.setGithubProfile(student.getGithubProfile());
                List<String> skills = student.getSkills().stream().map(Skill::getName).toList();
                requestDTO.setSkills(skills);
                requestDTO.setProfileImage(student.getProfileImage());
                requestDTO.setResumeUrl(student.getResumeUrl());
                List<EducationDTO> educationDTOList = student.getEducationList()
                        .stream()
                        .map(edu -> modelMapper.map(edu, EducationDTO.class))
                        .toList();
                requestDTO.setEducation(educationDTOList);

                return new ResponseEntity<>(requestDTO,HttpStatus.OK);
            }catch (Exception e){
                log.error("Error fetching student profile : {}",e.getMessage());
                return new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
            }
    }

    public ResponseEntity<?> getApplications(String userId) {
        User user = userRepository.findByEmail(userId);
        if (user == null) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

        Student student = studentRepository.findByUser(user);
        if (student == null) return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);

        List<ApplicationDTO> applicationDTOList = student.getApplications().stream().map(app -> {
            JobPost job = app.getJobPost();
            Recruiter recruiter = job.getRecruiter();
            return new ApplicationDTO(
                    app.getId(),
                    app.getAppliedDate(),
                    app.getStatus(),
                    job.getId(),
                    job.getTitle(),
                    recruiter.getCompanyName(),
                    job.getLocation(),
                    job.getType(),
                    job.getSalary(),
                    app.getCoverLetter(),
                    job.getLastDateToApply()
            );
        }).toList();
        if (applicationDTOList.isEmpty()) return new ResponseEntity<>("No Applications",HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(applicationDTOList, HttpStatus.OK);
    }

    private ValidatedStudent validateUserAndStudent(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new RuntimeException("User not found");

        Student student = studentRepository.findByUser(user);
        if (student == null) throw new RuntimeException("Student not found");

        return new ValidatedStudent(user, student);
    }


}
 record ValidatedStudent(User user, Student student) {}

