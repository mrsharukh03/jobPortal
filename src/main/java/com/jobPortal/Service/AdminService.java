package com.jobPortal.Service;

import com.jobPortal.DTO.AuthDTO.EmailDTO;
import com.jobPortal.Model.Users.User;
import com.jobPortal.Repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean activateUser(EmailDTO userIdDTO) {
        User user = userRepository.findByEmail(userIdDTO.getEmail());
        if (user == null) return false;
        user.setActive(true);
        userRepository.save(user);
        return true;
    }
}
