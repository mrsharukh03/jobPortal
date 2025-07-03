package com.jobPortal.Model;
import com.jobPortal.Enums.SkillLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // Optional: Track which users or job posts use this skill
    @ManyToMany(mappedBy = "skills")
    private List<Student> students = new ArrayList<>();

    @ManyToMany(mappedBy = "requiredSkills")
    private List<JobPost> jobPosts = new ArrayList<>();

}


