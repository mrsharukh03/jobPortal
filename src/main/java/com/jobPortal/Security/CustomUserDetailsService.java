package com.jobPortal.Security;

import com.jobPortal.Model.User;
import com.jobPortal.Repositorie.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {


    private final UserRepository userRepo;
    @Autowired
    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepo.findByEmail(email);
        if(user !=null){
            return new CustomUserDetails(user.getEmail(),user.getPassword(),user.getRole().toString());
        }
        throw new UsernameNotFoundException("User not found");
    }
}
