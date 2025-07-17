package com.jobPortal.Service;

import com.jobPortal.DTO.RecruiterDTO.RecruiterProfileDTO;
import com.jobPortal.Model.Users.Recruiter;
import com.jobPortal.Model.Users.User;
import com.jobPortal.Repositorie.RecruiterRepository;
import com.jobPortal.Repositorie.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service @Slf4j
public class RecruiterService {

    private final RecruiterRepository recruiterRepository;
    private final UserRepository userRepository;

    public RecruiterService(RecruiterRepository recruiterRepository, UserRepository userRepository) {
        this.recruiterRepository = recruiterRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> updateProfile(String username, RecruiterProfileDTO profileDTO) {
        try{
            ValidatedRecruiter validated = validateUserAndRecruiter(username);
            Recruiter recruiter = validated.recruiter();


            recruiter.setPhone(profileDTO.getPhone());
            recruiter.setDesignation(profileDTO.getDesignation());
            recruiter.setLinkedInProfile(profileDTO.getLinkedInProfile());
            recruiter.setProfileImageUrl(profileDTO.getProfileImageUrl());
            recruiter.setLocation(profileDTO.getLocation());
            recruiter.setCompanyName(profileDTO.getCompanyName());

            recruiterRepository.save(recruiter);
            return new ResponseEntity<>("Profile updated successfully", HttpStatus.OK);
        }catch (Exception e){
            log.error("Error updating recruiter profile {}",e.getMessage());
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private ValidatedRecruiter validateUserAndRecruiter(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new RuntimeException("User not found");
        Recruiter recruiter = recruiterRepository.findByUser(user);
        if (recruiter == null) throw new RuntimeException("Recruiter not found");
        return new ValidatedRecruiter(user, recruiter);
    }
}
record ValidatedRecruiter(User user, Recruiter recruiter) {}

