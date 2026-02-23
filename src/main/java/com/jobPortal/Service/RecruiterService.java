package com.jobPortal.Service;

import com.jobPortal.DTO.RecruiterDTO.RecruiterProfileDTO;
import com.jobPortal.DTO.RecruiterDTO.RecruiterViewDTO;
import com.jobPortal.Exception.BusinessException;
import com.jobPortal.Model.Users.Recruiter;
import com.jobPortal.Repository.RecruiterRepository;
import com.jobPortal.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public boolean updateProfile(UUID userId, RecruiterProfileDTO dto) {
        Optional<Recruiter> recruiteropt = recruiterRepository.findById(userId);
        if (recruiteropt.isEmpty()) {
            throw new BusinessException("Recruiter profile not found");
        }

            // 2️⃣ Find recruiter linked to user
            Recruiter recruiter = recruiteropt.get();

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
            return true;
    }

    public RecruiterViewDTO getProfile(UUID userId) {
            Optional<Recruiter> recruiteropt = recruiterRepository.findById(userId);
            if (recruiteropt.isEmpty()) {
                throw new BusinessException("Recruiter profile not found");
            }

            Recruiter recruiter = recruiteropt.get();

            RecruiterViewDTO recruiterProfileViewDTO = new RecruiterViewDTO(
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

            return recruiterProfileViewDTO;
    }


}
