package com.jobPortal.Repository;

import com.jobPortal.Model.Seeker.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    Optional<Object> findByUserIdAndJobId(UUID userId, Long jobId);

    int deleteByUserIdAndJobId(UUID userId, Long jobId);

    List<SavedJob> findByUserId(UUID userId);
}