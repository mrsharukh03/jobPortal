package com.jobPortal.Repository;

import com.jobPortal.Model.JobApplication;
import com.jobPortal.Model.JobPost;
import com.jobPortal.Model.Users.Seeker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication,Long> {
    boolean existsBySeekerAndJobPost(Seeker seeker, JobPost jobPost);

    List<JobApplication> findBySeeker_(Seeker seeker);

    List<JobApplication> findBySeeker_Id(UUID seekerId);
    Page<JobApplication> findByJobPost_Id(Long jobId, Pageable pageable);
}
