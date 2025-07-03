package com.jobPortal.Repositorie;

import com.jobPortal.Model.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends JpaRepository<Education,Long> {
    Education findByDegree(String degree);
}
