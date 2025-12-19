package com.jobPortal.Model;

import com.jobPortal.Enums.JobStatus;
import com.jobPortal.Enums.JobType;
import com.jobPortal.Model.Users.Recruiter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "job_posts")
@EntityListeners(AuditingEntityListener.class)
@Where(clause = "is_active = true")
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== Job Info =====
    @Column(nullable = false)
    private String title;

    @Column(length = 3000)
    private String description;

    private String location;

    @Enumerated(EnumType.STRING)
    private JobType type; // FULL_TIME, PART_TIME

    private String category;

    // ===== Salary =====
    private Integer minSalary;
    private Integer maxSalary;

    private String experienceRequired;

    // ===== Company Snapshot =====
    private String companyName;
    private String companyLogoUrl;

    // ===== Status =====
    @Enumerated(EnumType.STRING)
    private JobStatus status; // OPEN, CLOSED, EXPIRED, DRAFT

    private boolean isActive = true;

    // ===== Analytics =====
    private long viewCount;
    private int applicationsCount;

    // ===== Admin / Premium Hooks =====
    private boolean featured;          // future subscription
    private int priorityScore;          // sorting
    private boolean adminApproved;      // moderation

    // ===== Dates =====
    @CreatedDate
    private LocalDateTime postedDate;

    private LocalDate lastDateToApply;

    // ===== Relations =====
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

    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
    private List<JobApplication> applications = new ArrayList<>();
}
