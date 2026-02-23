package com.jobPortal.Repository;

import com.jobPortal.Model.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationRepo extends JpaRepository<EmailVerification,String> {

    EmailVerification findByEmail(String email);
}
