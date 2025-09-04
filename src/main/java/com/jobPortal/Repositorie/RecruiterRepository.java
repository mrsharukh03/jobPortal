package com.jobPortal.Repositorie;

import com.jobPortal.Model.Users.Recruiter;
import com.jobPortal.Model.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter, UUID> {

    Recruiter findByUser(User user);
    boolean existsByUser(User user);
}
