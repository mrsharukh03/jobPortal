package com.jobPortal.Model.Admin;

import com.jobPortal.Enums.Role;
import com.jobPortal.Model.Users.Admin;
import com.jobPortal.Model.Users.User;
import com.jobPortal.Repository.AdminRepository;
import com.jobPortal.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SuperAdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String email = "mrsharukh003@hotmail.com";

        if (userRepository.existsByEmail(email)) {
            return;
        }

        User user = new User();
        user.setEmail(email);
        user.setFullName("Super Admin");
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // unknown password
        user.setVerified(true);
        user.setActive(true);
        user.setRole(new ArrayList<>(List.of(Role.ADMIN, Role.SUPER_ADMIN)));
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userRepository.save(user);

        Admin admin = new Admin();
        admin.setUser(user);
        admin.setSuperAdmin(true);
        admin.setAdminSpecificInfo("System Super Administrator");

        adminRepository.save(admin);
    }
}

