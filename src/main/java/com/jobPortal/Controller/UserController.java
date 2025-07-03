package com.jobPortal.Controller;
import com.jobPortal.DTO.AuthDTO.LoginDTO;
import com.jobPortal.DTO.AuthDTO.SignupDTO;
import com.jobPortal.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {
    private final UserService userServices;
    public UserController(UserService userServices) {
        this.userServices = userServices;
    }

    @GetMapping("test")
    public ResponseEntity<String> test(){
        return new ResponseEntity<>("welcome", HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupDTO signupRequest){
        return userServices.signup(signupRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginRequest){
        return userServices.login(loginRequest);
    }

    @PostMapping("/verify/{email}")
    public ResponseEntity<?> verifyUser(@PathVariable String email){
        return userServices.verifyUser(email);
    }
}
