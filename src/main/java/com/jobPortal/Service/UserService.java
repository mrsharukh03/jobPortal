package com.jobPortal.Service;

import com.jobPortal.DTO.LoginDTO;
import com.jobPortal.DTO.SignupDTO;
import com.jobPortal.Enums.Role;
import com.jobPortal.Model.User;
import com.jobPortal.Repositorie.UserRepository;
import com.jobPortal.Security.JWTUtils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

    public UserService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, JWTUtils jwtUtils) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public ResponseEntity<?> signup(SignupDTO signupRequest) {
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

    public ResponseEntity<?> login(LoginDTO loginRequest) {
        if(!existsByEmail(loginRequest.getEmail())){
            return new ResponseEntity<>("User Not Exist", HttpStatus.NOT_FOUND);
        }

        try {
            User existingUser = userRepository.findByEmail(loginRequest.getEmail());
            if(existingUser != null){
                if(passwordEncoder.matches(loginRequest.getPassword(), existingUser.getPassword())) {
                    Map<String,String> response = new HashMap<>();
                    response.put("token", jwtUtils.generateToken(existingUser.getEmail(), existingUser.getRole().toString()));
                    response.put("refreshToken", jwtUtils.generateRefreshToken(existingUser.getEmail(), existingUser.getRole().toString()));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Invalid Password", HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>("User Not Exist", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error during login", e);
            return new ResponseEntity<>("Something went wrong!!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

}
