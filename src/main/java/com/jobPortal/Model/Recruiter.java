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
@Table(name = "recruiters")
public class Recruiter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @MapsId
    private User user;
    private String phone;
    @Lob
    @Column(name = "profile_image", columnDefinition = "LONGBLOB")
    private byte[] profileImage;
    private String companyName;
    private String designation; // e.g. HR, Manager, Recruiter
    private boolean isVerified = false; // Admin can verify
    private boolean isActive = true;

    // 🔗 Jobs posted by recruiter
    @OneToMany(mappedBy = "recruiter", cascade = CascadeType.ALL)
    private List<JobPost> jobPosts = new ArrayList<>();
}
