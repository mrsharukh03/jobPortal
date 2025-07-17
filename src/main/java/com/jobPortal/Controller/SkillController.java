package com.jobPortal.Controller;

import com.jobPortal.DTO.MultiUseDTO.SkillDTO;
import com.jobPortal.Service.SkillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skill")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    // Get skill by name
    @GetMapping("/{skillName}")
    public ResponseEntity<?> getSkillByName(@PathVariable String skillName){
        SkillDTO skillDTO = skillService.getSkillByName(skillName);
        if(skillDTO == null || skillDTO.getName() == null)
            return new ResponseEntity<>("Skill Not Found", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(skillDTO, HttpStatus.OK);
    }

    // Add single skill for logged-in user
    @PostMapping("/add/{skillName}")
    public ResponseEntity<?> addSkillToStudent(@PathVariable String skillName, @AuthenticationPrincipal UserDetails userDetails) {
        return skillService.addSkillToStudent(userDetails.getUsername(), skillName);
    }

    // Add multiple skills for logged-in user
    @PostMapping("/add-multiple")
    public ResponseEntity<?> addMultipleSkills(@RequestBody List<String> skillNames, @AuthenticationPrincipal UserDetails userDetails) {
        return skillService.addMultipleSkillsToStudent(userDetails.getUsername(), skillNames);
    }

    // Get all skills of logged-in user
    @GetMapping("/all")
    public ResponseEntity<?> getUserSkills(@AuthenticationPrincipal UserDetails userDetails){
        List<String> skills = skillService.getUserSkills(userDetails.getUsername());
        if(skills.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(skills);
    }

    // Delete a skill from logged-in user
    @DeleteMapping("/{skillName}")
    public ResponseEntity<?> deleteStudentSkill(@PathVariable String skillName, @AuthenticationPrincipal UserDetails userDetails) {
        return skillService.deleteStudentSkill(skillName, userDetails.getUsername());
    }
}
