package com.jobPortal.Repositorie;

import com.jobPortal.Model.Users.Seeker;
import com.jobPortal.Model.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SeekerRepository extends JpaRepository<Seeker, UUID> {

    Seeker findByUser(User user);
    boolean existsByUser(User user);
}
