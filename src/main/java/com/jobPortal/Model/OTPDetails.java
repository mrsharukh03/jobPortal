package com.jobPortal.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class OTPDetails {
    @Id
    private String email;
    private String otp;
    private LocalDateTime generatedTime;
    private LocalDateTime expirationTime;
    private LocalDateTime lastRequestTime;
    private int requestCount;

    public OTPDetails(String email, String otp, LocalDateTime generatedTime, LocalDateTime expirationTime, int requestCount, LocalDateTime lastRequestTime) {
        this.email = email;
        this.otp = otp;
        this.generatedTime = generatedTime;
        this.expirationTime = expirationTime;
        this.requestCount = requestCount;
        this.lastRequestTime = lastRequestTime;
    }

}
