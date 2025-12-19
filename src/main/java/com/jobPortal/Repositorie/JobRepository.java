package com.jobPortal.Repositorie;

import com.jobPortal.Model.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<JobPost,Long> {

}
