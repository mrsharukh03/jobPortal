package com.jobPortal.Model;

import com.jobPortal.Enums.MarriageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"profileImage", "resume"})
public class Student {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    private User user;
    private String phone;
    private String gender;
    private Date DOB;
    @Enumerated(EnumType.STRING)
    private MarriageStatus marriageStatus;

    @Column(unique = true, updatable = false)
    private String publicId;
    private String linkedinProfile;
    private String githubProfile;
    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "resume")
    private String resumeUrl;

    @ManyToMany
    @JoinTable(
            name = "student_skills",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educationList = new ArrayList<>();


    @OneToMany(mappedBy = "student")
    private List<JobApplication> applications = new ArrayList<>();

    @PrePersist
    public void initializePublicId() {
        if (this.publicId == null) {
            this.publicId = UUID.randomUUID().toString();
        }
    }
}
