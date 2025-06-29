package com.jobPortal.Model;

import com.jobPortal.Enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "job_applications")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date appliedDate = new Date();

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    // 🔗 User who applied
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    // 🔗 Job that was applied to
    @ManyToOne
    @JoinColumn(name = "job_post_id", nullable = false)
    private JobPost jobPost;
    private String coverLetter; // Optional
}
