package com.jobPortal.Controller;

import com.jobPortal.DTO.Signup;
import com.jobPortal.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userServices;
    public UserController(UserService userServices) {
        this.userServices = userServices;
    }

    @RequestMapping("test")
    public ResponseEntity<String> test(){
        return new ResponseEntity<>("welcome", HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody Signup signupRequest){
        return userServices.signup(signupRequest);
    }

}
