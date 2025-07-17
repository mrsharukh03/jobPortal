package com.jobPortal.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class OTPRequestDTO {
    @NotEmpty(message = "OTP is Required")
    private  String otp;
    @Email(message = "Email is Required")
    private String email;
}
