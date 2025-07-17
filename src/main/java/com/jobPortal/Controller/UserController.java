package com.jobPortal.Controller;

import com.jobPortal.DTO.AuthDTO.LoginDTO;
import com.jobPortal.DTO.AuthDTO.ForgetPasswordRequest;
import com.jobPortal.DTO.AuthDTO.SignupDTO;
import com.jobPortal.DTO.OTPRequestDTO;
import com.jobPortal.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userServices;

    public UserController(UserService userServices) {
        this.userServices = userServices;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupDTO signupRequest) {
        return userServices.signup(signupRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginRequest) {
        return userServices.login(loginRequest);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@Valid @RequestBody OTPRequestDTO requestDTO) {
        return userServices.verifyUser(requestDTO);
    }

    @PostMapping("/password/forget/{email}")
    public ResponseEntity<?> forgetPassword(@PathVariable String email) {
        return userServices.resendOTP(email);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> verifyOTP(@Valid @RequestBody ForgetPasswordRequest otpDto) {
        return userServices.resetPassword(otpDto);
    }

    @PostMapping("/otp/resend/{email}")
    public ResponseEntity<?> resendOTP(@PathVariable String email) {
        return userServices.resendOTP(email);
    }
}
