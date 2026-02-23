package com.jobPortal.Repository;

import com.jobPortal.Model.Seeker.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExperienceRepository extends JpaRepository<Experience,Long> {
    List<Experience> findBySeeker_Id(UUID userId);
}
