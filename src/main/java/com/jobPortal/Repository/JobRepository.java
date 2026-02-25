package com.jobPortal.Repository;

import com.jobPortal.Enums.JobStatus;
import com.jobPortal.Model.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<JobPost,Long> , JpaSpecificationExecutor<JobPost> {

    @Query("SELECT COALESCE(SUM(j.viewCount),0) FROM JobPost j WHERE j.recruiter.id = :recruiterId")
    long sumViewsByRecruiter(UUID recruiterId);
    // JobRepository.java
    @Query("SELECT j.status, COUNT(j) FROM JobPost j WHERE j.recruiter.id = :recruiterId GROUP BY j.status")
    List<Object[]> countJobsByStatusForRecruiter(@Param("recruiterId") UUID recruiterId);

}
