package com.jobPortal.Service;

import com.jobPortal.Repositorie.SeekerRepository;
import org.springframework.stereotype.Service;

@Service
public class JobSeekerService {

    private final SeekerRepository seekerRepository;


    public JobSeekerService(SeekerRepository seekerRepository) {
        this.seekerRepository = seekerRepository;
    }
}
