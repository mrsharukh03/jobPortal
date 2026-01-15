package com.jobPortal.Model.Users;

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
@ToString(exclude = {"profileImage", "resume"}) // Exclude large files from toString
public class Seeker {

    @Id
    private UUID id;

    private LocalDateTime createdTime;
    private LocalDateTime updateTime;

    private boolean isProfileComplete = false;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String phone;
    private String gender;
    private LocalDate DOB;

    @Enumerated(EnumType.STRING)
    private MarriageStatus marriageStatus;

    @Column(unique = true, updatable = false)
    private String publicId;

    private String linkedinProfile;
    private String githubProfile;
    private String portfolioUrl;
    @Column(length = 1000)
    private String bio;

    @Lob
    @Column(name = "profile_image", columnDefinition = "LONGBLOB")
    private byte[] profileImage;

    @Lob
    @Column(name = "resume", columnDefinition = "LONGBLOB")
    private byte[] resume;

    private String currentLocation;

    @ElementCollection
    private List<String> preferredLocations;

    @ElementCollection
    private List<String> languages;

    private Double expectedSalary;
    private String noticePeriod;
    private boolean currentlyWorking;

    @ManyToMany
    @JoinTable(
            name = "seeker_skills",
            joinColumns = @JoinColumn(name = "seeker_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "seeker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educationList = new ArrayList<>();

    @OneToMany(mappedBy = "seeker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experienceList = new ArrayList<>();

    @OneToMany(mappedBy = "seeker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certification> certifications = new ArrayList<>();

    @OneToMany(mappedBy = "seeker")
    private List<JobApplication> applications = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.createdTime = LocalDateTime.now();
        if (this.publicId == null) {
            this.publicId = UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}
