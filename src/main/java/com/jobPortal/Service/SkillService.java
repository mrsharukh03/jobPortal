package com.jobPortal.Service;

import com.jobPortal.DTO.MultiUseDTO.SkillDTO;
import com.jobPortal.Model.Skill;
import com.jobPortal.Model.Users.Student;
import com.jobPortal.Model.Users.User;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SkillService {

    private static final Logger log = LoggerFactory.getLogger(SkillService.class);

    private final SkillRepository skillRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public SkillService(SkillRepository skillRepository, StudentRepository studentRepository, UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    // Helper method to get student by email
    private Student getStudentByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new Exception("User not found");
        Student student = studentRepository.findByUser(user);
        if (student == null) throw new Exception("Student not found");
        return student;
    }

    // Add single skill to student
    public ResponseEntity<?> addSkillToStudent(String userEmail, String skillName) {
        try {
            Student student = getStudentByEmail(userEmail);

            Skill skill = skillRepository.findByNameIgnoreCase(skillName).orElse(null);
            if (skill == null) {
                skill = new Skill();
                skill.setName(skillName);
                skill.setStudents(new ArrayList<>());
                skill = skillRepository.save(skill);
            }

            if (student.getSkills().contains(skill)) {
                return new ResponseEntity<>("Skill already exists for student", HttpStatus.CONFLICT);
            }

            student.getSkills().add(skill);
            skill.getStudents().add(student);

            studentRepository.save(student);
            skillRepository.save(skill);

            return new ResponseEntity<>("Skill added successfully", HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error adding skill: {}", e.getMessage());
            return new ResponseEntity<>("Something went wrong!!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Add multiple skills to student
    public ResponseEntity<?> addMultipleSkillsToStudent(String userEmail, List<String> skillNames) {
        try {
            Student student = getStudentByEmail(userEmail);

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
            log.error("Error adding multiple skills: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    // Get all skills of a student
    public List<String> getUserSkills(String userEmail) {
        try {
            Student student = getStudentByEmail(userEmail);
            return student.getSkills().stream()
                    .map(Skill::getName)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding user skills: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    // Get SkillDTO by skill name
    public SkillDTO getSkillByName(String skillName){
        Optional<Skill> skill = skillRepository.findByNameIgnoreCase(skillName);
        if(skill.isPresent()){
            return modelMapper.map(skill.get(), SkillDTO.class);
        }
        return new SkillDTO();
    }

    // Delete skill from student
    public ResponseEntity<?> deleteStudentSkill(String skillName, String userEmail) {
        try {
            Student student = getStudentByEmail(userEmail);

            Optional<Skill> skillOpt = skillRepository.findByNameIgnoreCase(skillName);
            if (skillOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Skill not found");

            Skill skill = skillOpt.get();

            boolean removed = student.getSkills().remove(skill);
            if (!removed) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Skill not associated with student");

            skill.getStudents().remove(student);

            studentRepository.save(student);
            skillRepository.save(skill);

            return ResponseEntity.ok("Skill deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting student skill: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }
}
