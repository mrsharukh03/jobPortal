package com.jobPortal.Repository;

import com.jobPortal.Enums.ApplicationStatus;
import com.jobPortal.Model.JobApplication;
import com.jobPortal.Model.JobPost;
import com.jobPortal.Model.Users.Seeker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication,Long> {
    boolean existsBySeekerAndJobPost(Seeker seeker, JobPost jobPost);

    List<JobApplication> findBySeeker_Id(UUID seekerId);
    Page<JobApplication> findByJobPost_Id(Long jobId, Pageable pageable);

    @Query("SELECT COALESCE(AVG(a.aiMatchScore),0) FROM JobApplication a WHERE a.jobPost.recruiter.id = :recruiterId")
    Double avgAiScoreByRecruiter(UUID recruiterId);

    // JobApplicationRepository.java
    @Query("SELECT a.status, COUNT(a) FROM JobApplication a WHERE a.jobPost.recruiter.id = :recruiterId GROUP BY a.status")
    List<Object[]> countApplicationsByStatusForRecruiter(@Param("recruiterId") UUID recruiterId);
}
