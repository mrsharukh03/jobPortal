package com.jobPortal.Util;

import com.jobPortal.Model.Users.Recruiter;
import com.jobPortal.Model.Users.Seeker;
import com.jobPortal.Model.Users.User;
import com.jobPortal.Repository.RecruiterRepository;
import com.jobPortal.Repository.SeekerRepository;
import com.jobPortal.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserValidationHelper {

    private final UserRepository userRepository;
    private final SeekerRepository seekerRepository;
    private final RecruiterRepository recruiterRepository;


    private static final Logger log = LoggerFactory.getLogger(UserValidationHelper.class);

    public UserValidationHelper(UserRepository userRepository, SeekerRepository seekerRepository, RecruiterRepository recruiterRepository) {
        this.userRepository = userRepository;
        this.seekerRepository = seekerRepository;
        this.recruiterRepository = recruiterRepository;
    }

    public Seeker getSeekerByUserId(String username){
        try{
            User user = userRepository.findByEmail(username);
            if(user == null ) return null;
            return seekerRepository.findByUser(user);
        }catch (Exception e){
            log.error("An Error detected finding Job Seeker throw userid {}",e.getMessage());
            return null;
        }
    }


    public Recruiter getRecruiterByUserId(String username){
        try{
            User user = userRepository.findByEmail(username);
            if(user == null ) return null;
            return recruiterRepository.findByUser(user);
        }catch (Exception e){
            log.error("An Error detected finding recruiter throw userid {}",e.getMessage());
            return null;
        }
    }


}
