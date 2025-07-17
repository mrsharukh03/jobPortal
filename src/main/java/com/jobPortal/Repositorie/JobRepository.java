package com.jobPortal.Repositorie;

import com.jobPortal.Model.JobApplication;
import com.jobPortal.Model.JobPost;
import com.jobPortal.Model.Users.Recruiter;
import com.jobPortal.Model.Users.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<JobPost,Long> {
    JobPost findByRecruiter(Recruiter recruiter);
    Optional<JobPost> findByIdAndRecruiter_Id(Long jobId, Long recruiterId);

}
