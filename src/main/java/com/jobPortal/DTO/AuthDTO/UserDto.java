package com.jobPortal.DTO.AuthDTO;

import com.jobPortal.Enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class UserDto {
    private String fullName;
    private String email;
    private String profileUrl;
    private List<Role> role;
    private boolean isVerified;
}