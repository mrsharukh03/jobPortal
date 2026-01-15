package com.jobPortal.Service;

import com.jobPortal.DTO.RecruiterDTO.RecruiterProfileDTO;
import com.jobPortal.DTO.RecruiterDTO.RecruiterViewDTO;
import com.jobPortal.Model.Users.Recruiter;
import com.jobPortal.Model.Users.User;
import com.jobPortal.Repositorie.RecruiterRepository;
import com.jobPortal.Repositorie.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecruiterService {

    private static final Logger log = LoggerFactory.getLogger(RecruiterService.class);
    private final UserRepository userRepository;
    private final RecruiterRepository recruiterRepository;

    public RecruiterService(UserRepository userRepository,
                            RecruiterRepository recruiterRepository) {
        this.userRepository = userRepository;
        this.recruiterRepository = recruiterRepository;
    }

    public ResponseEntity<?> updateProfile(String username, RecruiterProfileDTO dto) {
        try{
            User user = userRepository.findByEmail(username);
            if(user == null){
                throw new RuntimeException("User not found");
            }

            // 2️⃣ Find recruiter linked to user
            Recruiter recruiter = recruiterRepository.findByUser(user);
            if(recruiter == null){
                throw new RuntimeException("Recruiter profile not found");
            }

            // 3️⃣ Map DTO → Entity
            recruiter.setPhone(dto.getPhone());
            recruiter.setCompanyLogoUrl(dto.getCompanyLogoUrl());
            recruiter.setLinkedInProfile(dto.getLinkedInProfile());
            recruiter.setCompanyWebsite(dto.getCompanyWebsite());

            recruiter.setCompanyName(dto.getCompanyName());
            recruiter.setDesignation(dto.getDesignation());
            recruiter.setLocation(dto.getLocation());
            recruiter.setIndustry(dto.getIndustry());
            recruiter.setCompanySize(dto.getCompanySize());
            recruiter.setCompanyDescription(dto.getCompanyDescription());

            recruiter.setYearsOfExperience(dto.getYearsOfExperience());
            recruiter.setAbout(dto.getAbout());
            recruiter.setHiringSkills(dto.getHiringSkills());

            recruiter.setProfileComplete(true);
            recruiter.setUpdateTime(LocalDateTime.now());

            // 4️⃣ Save
            recruiterRepository.save(recruiter);

            // 5️⃣ Response
            return ResponseEntity.ok(
                    "Recruiter profile updated successfully"
            );
        }catch(Exception e){
            log.error("Error while updating Recruiter Profile {}",e.getMessage());
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getProfile(UUID userId) {
        try {
            Optional<Recruiter> recruiteropt = recruiterRepository.findById(userId);
            if (recruiteropt.isEmpty()) {
                return new ResponseEntity<>("Recruiter profile not found", HttpStatus.NOT_FOUND);
            }
            Recruiter recruiter = recruiteropt.get();

            RecruiterViewDTO dto = new RecruiterViewDTO(
                    recruiter.getId(),
                    recruiter.getPhone(),
                    recruiter.getCompanyLogoUrl(),
                    recruiter.getLinkedInProfile(),
                    recruiter.getCompanyWebsite(),
                    recruiter.getCompanyName(),
                    recruiter.getDesignation(),
                    recruiter.getLocation(),
                    recruiter.getIndustry(),
                    recruiter.getCompanySize(),
                    recruiter.getCompanyDescription(),
                    recruiter.getYearsOfExperience(),
                    recruiter.getAbout(),
                    recruiter.getHiringSkills(),
                    recruiter.isProfileComplete(),
                    recruiter.getCreatedTime(),
                    recruiter.getUpdateTime()
            );

            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            log.error("Error fetching recruiter profile: {}", e.getMessage());
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
