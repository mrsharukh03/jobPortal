package com.jobPortal.Service;

import com.jobPortal.DTO.JobSeekerDTO.PersonalDetailDTO;
import com.jobPortal.Repositorie.SeekerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class JobSeekerService {

    private final SeekerRepository seekerRepository;


    public JobSeekerService(SeekerRepository seekerRepository) {
        this.seekerRepository = seekerRepository;
    }

    public ResponseEntity<?> updatePersonalDetails(PersonalDetailDTO personalDetailDTO, String username) {

        return null;
    }
}
