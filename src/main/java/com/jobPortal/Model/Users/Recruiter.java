package com.jobPortal.Model.Users;

import com.jobPortal.Model.JobPost;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "recruiters")
public class Recruiter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ===== Contact =====
    @Column(nullable = false)
    private String phone;

    // ===== Company Info =====
    @Column(nullable = false)
    private String companyName;

    private String companyLogoUrl;
    private String industry;
    private String companySize;
    private String companyWebsite;

    @Column(length = 1000)
    private String companyDescription;

    // ===== Recruiter Info =====
    private String designation;
    private int yearsOfExperience;
    private String location;

    @Column(length = 500)
    private String about;

    // ===== Social =====
    private String linkedInProfile;

    // ===== Hiring =====
    @ElementCollection
    private List<String> hiringSkills = new ArrayList<>();

    // ===== Status / Analytics =====
    private boolean isProfileComplete;
    private int totalJobsPosted;
    private int totalHires;

    // ===== Future Subscription Hook =====
    private boolean premiumRecruiter;
    private LocalDate premiumExpiry;

    // ===== Auditing =====
    private LocalDateTime createdTime;
    private LocalDateTime updateTime;

    @OneToMany(mappedBy = "recruiter", cascade = CascadeType.ALL)
    private List<JobPost> jobPosts = new ArrayList<>();

    @PrePersist
    void onCreate() {
        createdTime = LocalDateTime.now();
        updateTime = createdTime;
    }

    @PreUpdate
    void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
