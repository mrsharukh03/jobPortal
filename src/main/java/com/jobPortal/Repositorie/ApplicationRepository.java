package com.jobPortal.Repositorie;

import com.jobPortal.Model.JobApplication;
import com.jobPortal.Model.JobPost;
import com.jobPortal.Model.Users.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<JobApplication,Long> {
    Optional<JobApplication> findByIdAndJobPost_Recruiter_Id(Long applicationId, Long recruiterId);

    boolean existsByStudentAndJobPost(Student student, JobPost jobPost);
}
