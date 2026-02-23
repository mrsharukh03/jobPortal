package com.jobPortal.Repository;

import com.jobPortal.Model.Seeker.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    List<Certification> findBySeeker_Id(UUID userId);
}
