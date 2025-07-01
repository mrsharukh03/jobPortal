package com.jobPortal.Service;

import com.jobPortal.DTO.Signup;
import com.jobPortal.Enums.Role;
import com.jobPortal.Model.User;
import com.jobPortal.Repositorie.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> signup(Signup signupRequest) {
        if(!existsByEmail(signupRequest.getEmail())){
            User user = new User();
            user.setFullName(signupRequest.getFullName());
            user.setEmail(signupRequest.getEmail());
            user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
            user.setActive(false);
                user.setRole(signupRequest.getRole());
            if(!signupRequest.getRole().equals(Role.ADMIN)){
            } else {
                return new ResponseEntity<>("Signup as Admin is not Allowed", HttpStatus.BAD_REQUEST);
            }
            try {
                userRepository.save(user);
            } catch (Exception e) {
                log.error("Error during Signup", e);
                return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>("Please Verify your email", HttpStatus.OK);
        }
        return new ResponseEntity<>("User Already Exist", HttpStatus.CONFLICT);
    }

    private boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }
}
