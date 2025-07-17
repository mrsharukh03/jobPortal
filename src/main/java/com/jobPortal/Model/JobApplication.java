package com.jobPortal.Model;

import com.jobPortal.Enums.ApplicationStatus;
import com.jobPortal.Model.Users.Student;
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
    private boolean selection = false;

    // 🔗 Students who applied
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    // 🔗 Job that was applied to
    @ManyToOne
    @JoinColumn(name = "job_post_id", nullable = false)
    private JobPost jobPost;
    private String resumeUrl;
    private String coverLetter; // Optional
}
