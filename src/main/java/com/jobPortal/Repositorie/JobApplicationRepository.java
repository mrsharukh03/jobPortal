package com.jobPortal.Repositorie;

import com.jobPortal.Model.JobApplication;
import com.jobPortal.Model.JobPost;
import com.jobPortal.Model.Users.Seeker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<JobApplication,Long> {
    Optional<JobApplication> findByIdAndJobPost_Recruiter_Id(Long applicationId, UUID recruiterId);

    boolean existsBySeekerAndJobPost(Seeker seeker, JobPost jobPost);
}
