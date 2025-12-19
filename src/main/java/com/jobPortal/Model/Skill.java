package com.jobPortal.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jobPortal.Model.Users.Seeker;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"seekers", "jobPosts"})
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "skills")
    private List<Seeker> seekers = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "requiredSkills")
    private List<JobPost> jobPosts = new ArrayList<>();

    @PrePersist
    @PreUpdate
    public void normalizeSkillName() {
        if (name != null) {
            this.name = name.trim().toLowerCase();
        }
    }
}
