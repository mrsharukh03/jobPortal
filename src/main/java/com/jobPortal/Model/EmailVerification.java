package com.jobPortal.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerification {
    @Id
    private String token;
    private String email;
    private LocalDateTime generationTime;
    private LocalDateTime expirationTime;
}
