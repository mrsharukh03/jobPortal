package com.jobPortal.Service;

import com.jobPortal.DTO.AuthDTO.UserProfile;
import com.jobPortal.Enums.Role;
import com.jobPortal.Exception.UserNotFoundException;
import com.jobPortal.Model.Users.Recruiter;
import com.jobPortal.Model.Users.Seeker;
import com.jobPortal.Model.Users.User;
import com.jobPortal.Repository.RecruiterRepository;
import com.jobPortal.Repository.SeekerRepository;
import com.jobPortal.Repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserQueryService {

    private final UserRepository userRepository;
    private final SeekerRepository seekerRepository;
    private final RecruiterRepository recruiterRepository;

    public UserQueryService(
            UserRepository userRepository,
            SeekerRepository seekerRepository,
            RecruiterRepository recruiterRepository
    ) {
        this.userRepository = userRepository;
        this.seekerRepository = seekerRepository;
        this.recruiterRepository = recruiterRepository;
    }

    public List<String> getAlerts(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null)
            throw new UserNotFoundException("User not found");

        List<String> alerts = new ArrayList<>();

        if (!user.isVerified())
            alerts.add("Email verification pending");

        if (user.getRole().contains(Role.SEEKER)) {
            Seeker seeker = seekerRepository.findByUser(user);
            if (seeker == null || !seeker.isProfileComplete())
                alerts.add("Seeker profile incomplete");
        }

        if (user.getRole().contains(Role.RECRUITER)) {
            Recruiter recruiter = recruiterRepository.findByUser(user);
            if (recruiter == null || !recruiter.isProfileComplete())
                alerts.add("Recruiter profile incomplete");
        }

        return alerts;
    }

    public UserProfile getUserProfile(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null)
            throw new UserNotFoundException("User not found");

        UserProfile profile = new UserProfile();
        profile.setFullName(user.getFullName());
        profile.setEmail(user.getEmail());
        profile.setActive(user.isActive());
        profile.setVerified(user.isVerified());
        profile.setProfileURL(user.getProfileUrl());
        profile.setCreatedTime(user.getCreatedTime());

        return profile;
    }

    public String getProfileImage(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null)
            throw new UserNotFoundException("User not found");

        return user.getProfileUrl();
    }
}
