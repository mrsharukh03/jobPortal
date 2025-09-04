package com.jobPortal.Repositorie;

import com.jobPortal.Model.JobPost;
import com.jobPortal.Model.Users.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<JobPost,Long> {
    JobPost findByRecruiter(Recruiter recruiter);

}
