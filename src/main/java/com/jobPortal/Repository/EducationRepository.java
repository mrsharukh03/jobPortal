package com.jobPortal.Repository;

import com.jobPortal.Model.Seeker.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EducationRepository extends JpaRepository<Education,Long> {
    Education findByDegree(String degree);

    List<Education> findBySeeker_Id(UUID userId);
}
