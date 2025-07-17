package com.jobPortal.Repositorie;

import com.jobPortal.Model.Users.Recruiter;
import com.jobPortal.Model.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter,Long> {

    Recruiter findByUser(User user);
}
