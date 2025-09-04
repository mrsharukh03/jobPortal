package com.jobPortal.DTO.AuthDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class TokenVerificationResult {
    private boolean success;
    private String email;
    private String message;

    public TokenVerificationResult(boolean success, String email, String message) {
        this.success = success;
        this.email = email;
        this.message = message;
    }
}

