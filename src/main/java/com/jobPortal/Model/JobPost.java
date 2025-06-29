package com.jobPortal.Model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "job_posts")
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String location;
    private String type; // Full-time, Internship, remote etc.
    private String category;
    private int salary;
    private String experienceRequired;

    private Date postedDate = new Date();
    private Date lastDateToApply;

    @ManyToOne
    @JoinColumn(name = "recruiter_id")
    private Recruiter recruiter;

    @ManyToMany
    @JoinTable(
            name = "job_required_skills",
            joinColumns = @JoinColumn(name = "job_post_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> requiredSkills = new ArrayList<>();

    @OneToMany(mappedBy = "jobPost")
    private List<JobApplication> applications = new ArrayList<>();

}
