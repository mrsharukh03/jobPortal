package com.jobPortal.Service;

import com.jobPortal.DTO.StudentDTO.EducationDTO;
import com.jobPortal.Model.Education;
import com.jobPortal.Model.Skill;
import com.jobPortal.Model.Student;
import com.jobPortal.Model.User;
import com.jobPortal.Repositorie.EducationRepository;
import com.jobPortal.Repositorie.SkillRepository;
import com.jobPortal.Repositorie.StudentRepository;
import com.jobPortal.Repositorie.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillService {

    private static final Logger log = LoggerFactory.getLogger(SkillService.class);
    private final SkillRepository skillRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    @Autowired
    private ModelMapper modelMapper;

    public SkillService(SkillRepository skillRepository, StudentRepository studentRepository, UserRepository userRepository, EducationRepository educationRepository) {
        this.skillRepository = skillRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.educationRepository = educationRepository;
    }

    public ResponseEntity<?> addSkillToStudent(String userId, String skillName) {
        try {
            User user = userRepository.findByEmail(userId);
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            Student student = studentRepository.findByUser(user);
            if (student == null) {
                return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
            }

            Skill skill = skillRepository.findByNameIgnoreCase(skillName).orElse(null);
            if (skill == null) {
                skill = new Skill();
                skill.setName(skillName);
                skill.setStudents(new ArrayList<>());
            }

            if (!student.getSkills().contains(skill)) {
                student.getSkills().add(skill);
                skill.getStudents().add(student);
            } else {
                return new ResponseEntity<>("Skill already exists for student", HttpStatus.CONFLICT);
            }

            skillRepository.save(skill);
            studentRepository.save(student);

            return new ResponseEntity<>("Skill added successfully", HttpStatus.OK);

        } catch (Exception e) {
            log.error("Something went wrong: {}", e.getMessage());
            return new ResponseEntity<>("Something went wrong!!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> addMultipleSkillsToStudent(String email, List<String> skillNames) {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

            Student student = studentRepository.findByUser(user);
            if (student == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");

            for (String skillName : skillNames) {
                Skill skill = skillRepository.findByNameIgnoreCase(skillName).orElseGet(() -> {
                    Skill newSkill = new Skill();
                    newSkill.setName(skillName);
                    newSkill.setStudents(new ArrayList<>());
                    return skillRepository.save(newSkill);
                });

                if (!student.getSkills().contains(skill)) {
                    student.getSkills().add(skill);
                    skill.getStudents().add(student);
                }
            }

            studentRepository.save(student);
            return ResponseEntity.ok("Skills added successfully");

        } catch (Exception e) {
            log.error("Error adding skills: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    public List<String> getUserSkills(String userId){
        try{
            User user = userRepository.findByEmail(userId);
            if (user == null) return new ArrayList<>();

            Student student = studentRepository.findByUser(user);
            if (student == null) return new ArrayList<>();
            List<String> skills = student.getSkills().stream()
                    .map(Skill::getName)
                    .collect(Collectors.toList());
            return skills;
        }catch (Exception e){
            log.error("Error finding user skill: {}",e.getMessage());
        }
        return new ArrayList<>();
    }

    public ResponseEntity<?> updateEducation(String userId, EducationDTO educationDTO){
        try {
            User user = userRepository.findByEmail(userId);
            if (user == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

            Student student = studentRepository.findByUser(user);
            if (student == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");

            // Check if education already exists
            boolean alreadyExists = student.getEducationList().stream()
                    .anyMatch(e -> e.getDegree().equalsIgnoreCase(educationDTO.getDegree()) ||
                            e.getFieldOfStudy().equalsIgnoreCase(educationDTO.getFieldOfStudy()) &&
                            e.getCollegeName().equalsIgnoreCase(educationDTO.getCollegeName()) &&
                            e.getStartYear() == educationDTO.getStartYear() &&
                            e.getEndYear() == educationDTO.getEndYear());

            if (alreadyExists) {
                return new ResponseEntity<>("Education already exists", HttpStatus.ALREADY_REPORTED);
            }
            Education education = modelMapper.map(educationDTO, Education.class);
            education.setStudent(student);

            educationRepository.save(education);
            student.getEducationList().add(education);
            studentRepository.save(student);

            return new ResponseEntity<>("Education added successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error adding education for user {}: {}", userId, e.getMessage(), e);
            return new ResponseEntity<>("An error occurred while saving education.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
