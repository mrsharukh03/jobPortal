package com.jobPortal.DTO.AuthDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class UserProfile {

    private String fullName;
    private String profileURL;
    private String profileUrl;
    private String email;
    private boolean isActive = false;
    private boolean isVerified = false;
    private LocalDateTime createdTime;

}
