package com.jobPortal.Model;

import com.jobPortal.Enums.JobStatus;
import com.jobPortal.Enums.JobType;
import com.jobPortal.Model.Users.Recruiter;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "job_posts")
@EntityListeners(AuditingEntityListener.class)
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String location;

    @Enumerated(EnumType.STRING)
    private JobType type = JobType.FULL_TIME;

    private String category;
    private int salary;
    private String experienceRequired;
    private String companyName;
    private String companyLogoUrl;


    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date postedDate;

    @Temporal(TemporalType.DATE)
    private Date lastDateToApply;

    @Enumerated(EnumType.STRING)
    private JobStatus status = JobStatus.OPEN;

    private boolean isActive = true;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    private Recruiter recruiter;

    @ManyToMany
    @JoinTable(
            name = "job_required_skills",
            joinColumns = @JoinColumn(name = "job_post_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> requiredSkills = new ArrayList<>();
    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> applications = new ArrayList<>();
}
