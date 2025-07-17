package com.jobPortal.Model.Users;

import com.jobPortal.Model.JobPost;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "recruiters")
public class Recruiter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @MapsId
    private User user;

    private String phone;
    private String profileImageUrl;
    private String linkedInProfile;
    private String companyName;
    private String designation; // HR, Manager, etc.
    private String location;
    private boolean isActive = true;

    @OneToMany(mappedBy = "recruiter", cascade = CascadeType.ALL)
    private List<JobPost> jobPosts = new ArrayList<>();

}
