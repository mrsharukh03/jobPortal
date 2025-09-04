package com.jobPortal.Model;
import com.jobPortal.Model.Users.Seeker;
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

    @ManyToMany(mappedBy = "skills")
    private List<Seeker> seekers = new ArrayList<>();

    @ManyToMany(mappedBy = "requiredSkills")
    private List<JobPost> jobPosts = new ArrayList<>();

}


