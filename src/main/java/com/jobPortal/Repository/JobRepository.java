package com.jobPortal.Repository;

import com.jobPortal.Model.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<JobPost,Long> , JpaSpecificationExecutor<JobPost> {
    List<JobPost> findAllByCategory(String category);

    List<JobPost> findByCategoryIgnoreCase(String category);
    Page<JobPost> findByRecruiter_User_Email(String email, Pageable pageable);

}
