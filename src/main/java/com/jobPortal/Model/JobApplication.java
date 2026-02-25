package com.jobPortal.Model;

import com.jobPortal.Enums.ApplicationStatus;
import com.jobPortal.Model.Users.Seeker;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

        import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "job_applications",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"seeker_id", "job_post_id"})
        }
)
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime appliedAt;

    private LocalDateTime lastUpdatedAt;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private String resumeUrl;

    @Column(length = 2000)
    private String coverLetter;

    private String aiSummary;

    @Min(1)
    @Max(5)
    private Integer rating;

    @Column(length = 2000)
    private String recruiterNotes;

    private LocalDate interviewDate;

    @Column(length = 3000)
    private String interviewFeedback;

    private String rejectionReason;

    @Min(0)
    @Max(100)
    private Integer aiMatchScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seeker_id", nullable = false)
    private Seeker seeker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_id", nullable = false)
    private JobPost jobPost;

    private LocalDateTime shortlistedAt;
    private LocalDateTime selectedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime interviewScheduledAt;

    @PrePersist
    public void onCreate() {
        this.appliedAt = LocalDateTime.now();
        this.lastUpdatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ApplicationStatus.PENDING;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.lastUpdatedAt = LocalDateTime.now();
    }
}