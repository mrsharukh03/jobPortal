package com.jobPortal.Repositorie;

import com.jobPortal.Model.OTPDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OTPDetailsRepository extends JpaRepository<OTPDetails,String> {
}
