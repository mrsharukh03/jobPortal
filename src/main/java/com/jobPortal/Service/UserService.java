package com.jobPortal.Service;

import com.jobPortal.DTO.AuthDTO.LoginDTO;
import com.jobPortal.DTO.AuthDTO.ForgetPasswordRequest;
import com.jobPortal.DTO.AuthDTO.SignupDTO;
import com.jobPortal.DTO.OTPRequestDTO;
import com.jobPortal.Enums.Role;
import com.jobPortal.Model.Users.Admin;
import com.jobPortal.Model.Users.Recruiter;
import com.jobPortal.Model.Users.Student;
import com.jobPortal.Model.Users.User;
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
        if(existsByEmail(signupRequest.getEmail())){
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
                OTPServices.sendOTP(user.getEmail());
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
        if(existsByEmail(loginRequest.getEmail())){
            return new ResponseEntity<>("User Not Exist", HttpStatus.NOT_FOUND);
        }
        try {
            User existingUser = userRepository.findByEmail(loginRequest.getEmail());
            if(existingUser == null) return new ResponseEntity<>("User Not Exist", HttpStatus.NOT_FOUND);
            if(!existingUser.isVerified()) return new ResponseEntity<>("Please verify your email", HttpStatus.BAD_REQUEST);
            if(passwordEncoder.matches(loginRequest.getPassword(), existingUser.getPassword())) {
                    Map<String,String> response = new HashMap<>();
                    response.put("token", jwtUtils.generateToken(existingUser.getEmail(), existingUser.getRole().toString()));
                    response.put("refreshToken", jwtUtils.generateRefreshToken(existingUser.getEmail(), existingUser.getRole().toString()));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Invalid Password", HttpStatus.UNAUTHORIZED);
                }
        } catch (Exception e) {
            log.error("Error during login", e);
            return new ResponseEntity<>("Something went wrong!!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean existsByEmail(String email){
        return !userRepository.existsByEmail(email);
    }

    public ResponseEntity<?> verifyUser(OTPRequestDTO requestDTO) {
        if(existsByEmail(requestDTO.getEmail())) return new ResponseEntity<>("User not exist",HttpStatus.NOT_FOUND);
        try{
        User user = userRepository.findByEmail(requestDTO.getEmail());
        if (user.isVerified()){
            return new ResponseEntity<>("User Already Verified",HttpStatus.ALREADY_REPORTED);
        }
            Map<String, String> verification = OTPServices.verifyOTP(requestDTO.getEmail(), requestDTO.getOtp());
        if (verification.get("status").equalsIgnoreCase("false")){
            return new ResponseEntity<>("Verification Failed"+verification.get("msg"),HttpStatus.BAD_REQUEST);
        }
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
        }catch (Exception e){
            log.error("Error verifying user");
            return new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> resendOTP(String email) {
        try {
            User user = userRepository.findByEmail(email);
            if(user == null) return new ResponseEntity<>("User Not found",HttpStatus.NOT_FOUND);
            Map<String,String> isSentOTP = OTPServices.sendOTP(user.getEmail());
            if(isSentOTP.get("status").equalsIgnoreCase("false")){
                return new ResponseEntity<>(isSentOTP.get("msg"),HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("We have sent an otp",HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> resetPassword(ForgetPasswordRequest passeordRequest) {
        if(passeordRequest.getEmail() == null || passeordRequest.getPassword() == null || passeordRequest.getOtp() == null){
            return new ResponseEntity<>("Invalid Argument",HttpStatus.BAD_REQUEST);
        }
        try {
            User user = userRepository.findByEmail(passeordRequest.getEmail());
            if(user == null) return new ResponseEntity<>("User Not found",HttpStatus.NOT_FOUND);
            Map<String, String> verification = OTPServices.verifyOTP(passeordRequest.getEmail(), passeordRequest.getOtp());
            if(verification.get("status").equalsIgnoreCase("true")){
                user.setPassword(passwordEncoder.encode(passeordRequest.getPassword()));
                userRepository.save(user);
                return new ResponseEntity<>("Password change successfully",HttpStatus.OK);
            }else {
                return new ResponseEntity<>(verification.get("msg"),HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
          log.error("error resenting password {}",e.getMessage());
          return new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
