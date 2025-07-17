package com.jobPortal.Service;

import com.jobPortal.DTO.StudentDTO.EducationDTO;
import com.jobPortal.Model.Education;
import com.jobPortal.Model.Users.Student;
import com.jobPortal.Model.Users.User;
import com.jobPortal.Repositorie.EducationRepository;
import com.jobPortal.Repositorie.StudentRepository;
import com.jobPortal.Repositorie.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EducationService {

    private static final Logger log = LoggerFactory.getLogger(EducationService.class);

    private final EducationRepository educationRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public EducationService(EducationRepository educationRepository,
                            StudentRepository studentRepository,
                            UserRepository userRepository,
                            ModelMapper modelMapper) {
        this.educationRepository = educationRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public ResponseEntity<String> addEducation(String userEmail, EducationDTO educationDTO) {
        try {
            User user = userRepository.findByEmail(userEmail);
            if (user == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

            Student student = studentRepository.findByUser(user);
            if (student == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");

            boolean alreadyExists = student.getEducationList().stream()
                    .anyMatch(e -> e.getDegree().equalsIgnoreCase(educationDTO.getDegree()) &&
                            e.getFieldOfStudy().equalsIgnoreCase(educationDTO.getFieldOfStudy()) &&
                            e.getCollegeName().equalsIgnoreCase(educationDTO.getCollegeName()) &&
                            e.getStartYear() == educationDTO.getStartYear() &&
                            e.getEndYear() == educationDTO.getEndYear());

            if (alreadyExists) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Education already exists");
            }

            Education education = modelMapper.map(educationDTO, Education.class);
            education.setStudent(student);

            educationRepository.save(education);
            student.getEducationList().add(education);
            studentRepository.save(student);

            return ResponseEntity.ok("Education added successfully");

        } catch (Exception e) {
            log.error("Error adding education for user {}: {}", userEmail, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while saving education.");
        }
    }

    public ResponseEntity<String> deleteEducation(String userEmail, String degreeName) {
        try {
            User user = userRepository.findByEmail(userEmail);
            if (user == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

            Student student = studentRepository.findByUser(user);
            if (student == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");

            Optional<Education> educationOpt = student.getEducationList().stream()
                    .filter(education -> education.getDegree().equalsIgnoreCase(degreeName))
                    .findFirst();

            if (educationOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Education not found");
            }

            Education education = educationOpt.get();
            student.getEducationList().remove(education);
            educationRepository.delete(education);
            studentRepository.save(student);

            return ResponseEntity.ok("Education deleted successfully");

        } catch (Exception e) {
            log.error("Error deleting education for user {}: {}", userEmail, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    public List<EducationDTO> getAllEducations(String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail);
            if (user == null) return List.of();

            Student student = studentRepository.findByUser(user);
            if (student == null) return List.of();

            return student.getEducationList().stream()
                    .map(education -> modelMapper.map(education, EducationDTO.class))
                    .toList();

        } catch (Exception e) {
            log.error("Error fetching educations for user {}: {}", userEmail, e.getMessage());
            return List.of();
        }
    }

    // Service methods in EducationService (or SkillService if aap waha rakh rahe ho)

    public List<EducationDTO> getAllEducation(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) return new ArrayList<>();

        Student student = studentRepository.findByUser(user);
        if (student == null) return new ArrayList<>();

        return student.getEducationList().stream()
                .map(education -> modelMapper.map(education, EducationDTO.class))
                .collect(Collectors.toList());
    }

    public EducationDTO getEducationByDegree(String userEmail, String degreeName) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) return null;

        Student student = studentRepository.findByUser(user);
        if (student == null) return null;

        Optional<Education> educationOpt = student.getEducationList().stream()
                .filter(edu -> edu.getDegree().equalsIgnoreCase(degreeName))
                .findFirst();

        return educationOpt.map(edu -> modelMapper.map(edu, EducationDTO.class)).orElse(null);
    }

    public ResponseEntity<String> updateEducationByDegree(String userEmail, String degreeName, EducationDTO educationDTO) {
        try {
            User user = userRepository.findByEmail(userEmail);
            if (user == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

            Student student = studentRepository.findByUser(user);
            if (student == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");

            Optional<Education> educationOpt = student.getEducationList().stream()
                    .filter(edu -> edu.getDegree().equalsIgnoreCase(degreeName))
                    .findFirst();

            if (educationOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Education not found");
            }

            Education education = educationOpt.get();

            // Update fields
            education.setDegree(educationDTO.getDegree());
            education.setFieldOfStudy(educationDTO.getFieldOfStudy());
            education.setCollegeName(educationDTO.getCollegeName());
            education.setStartYear(educationDTO.getStartYear());
            education.setEndYear(educationDTO.getEndYear());

            educationRepository.save(education);

            return ResponseEntity.ok("Education updated successfully");
        } catch (Exception e) {
            log.error("Error updating education for user {}: {}", userEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

}
