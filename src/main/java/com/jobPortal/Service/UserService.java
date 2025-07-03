package com.jobPortal.Service;

import com.jobPortal.DTO.AuthDTO.LoginDTO;
import com.jobPortal.DTO.AuthDTO.SignupDTO;
import com.jobPortal.Enums.Role;
import com.jobPortal.Model.Admin;
import com.jobPortal.Model.Recruiter;
import com.jobPortal.Model.Student;
import com.jobPortal.Model.User;
import com.jobPortal.Repositorie.AdminRepository;
import com.jobPortal.Repositorie.RecruiterRepository;
import com.jobPortal.Repositorie.StudentRepository;
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
    private final StudentRepository studentRepository;
    private final RecruiterRepository recruiterRepository;
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

    public UserService(UserRepository userRepository, StudentRepository studentRepository, RecruiterRepository recruiterRepository, AdminRepository adminRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, JWTUtils jwtUtils) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.recruiterRepository = recruiterRepository;
        this.adminRepository = adminRepository;
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

    public ResponseEntity<?> verifyUser(String email) {
        if(!existsByEmail(email)) return new ResponseEntity<>("User not exist",HttpStatus.NOT_FOUND);
        try{
        User user = userRepository.findByEmail(email);
        if (!user.isVerified()){
            user.setVerified(true);
            userRepository.save(user);
            if(user.getRole() == Role.STUDENT){
                Student newStudent = new Student();
                newStudent.setUser(user);
                studentRepository.save(newStudent);
            } else if (user.getRole() == Role.RECRUITER) {
                Recruiter newRecuriter = new Recruiter();
                newRecuriter.setUser(user);
                recruiterRepository.save(newRecuriter);
            }else {
                Admin admin = new Admin();
                admin.setUser(user);
                adminRepository.save(admin);
            }
        return new ResponseEntity<>("Verification Success",HttpStatus.OK);
        }
        return new ResponseEntity<>("User Alerdy Verified",HttpStatus.ALREADY_REPORTED);
        }catch (Exception e){
            log.error("Error verifying user");
            return new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
