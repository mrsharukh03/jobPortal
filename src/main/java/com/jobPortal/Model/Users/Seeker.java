package com.jobPortal.Model.Users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jobPortal.Enums.MarriageStatus;
import com.jobPortal.Model.Seeker.Certification;
import com.jobPortal.Model.Seeker.Education;
import com.jobPortal.Model.Seeker.Experience;
import com.jobPortal.Model.JobApplication;
import com.jobPortal.Model.Skill;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {
        "profileImage", "resumeText", "educationList", "experienceList",
        "certifications", "applications", "skills", "user"
})
public class Seeker {

    @Id
    private UUID id;
    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
    private boolean isProfileComplete = false;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // Personal Info
    private String phone;
    private String gender;
    private LocalDate DOB;

    @Enumerated(EnumType.STRING)
    private MarriageStatus marriageStatus;

    @Column(unique = true, updatable = false)
    private String publicId;

    @Column(length = 1000)
    private String bio;

    @Column(length = 2000)
    private String careerObjective;

    @ElementCollection
    private List<String> softSkills = new ArrayList<>();

    // Professional Info
    @ManyToMany
    @JoinTable(
            name = "seeker_skills",
            joinColumns = @JoinColumn(name = "seeker_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skills = new ArrayList<>();

    private String currentLocation;
    @ElementCollection
    private List<String> preferredLocations = new ArrayList<>();
    @ElementCollection
    private List<String> languages = new ArrayList<>();

    private Double expectedSalary;
    private String noticePeriod;
    private boolean currentlyWorking;

    // Social & Portfolio
    private String linkedinProfile;
    private String githubProfile;
    private String portfolioUrl;

    // AI & Resume Features
    private String resumeUrl;

    @Lob
    private String resumeText;

    @Lob
    private String aiSummary;

    @ElementCollection
    private List<String> aiRecommendedSkills = new ArrayList<>();

    // Attachments
    @Lob
    @JsonIgnore
    private byte[] profileImage;

    // Relations
    @OneToMany(mappedBy = "seeker", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Education> educationList = new ArrayList<>();

    @OneToMany(mappedBy = "seeker", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Experience> experienceList = new ArrayList<>();

    @OneToMany(mappedBy = "seeker", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Certification> certifications = new ArrayList<>();

    @OneToMany(mappedBy = "seeker")
    @JsonIgnore
    private List<JobApplication> applications = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.createdTime = LocalDateTime.now();
        if (this.publicId == null) this.publicId = UUID.randomUUID().toString();
    }

    @PreUpdate
    public void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}