package com.jobPortal.DTO.AuthDTO;

import lombok.Data;

@Data
public class ResetPasswordDTO {
    private String token;
    private String password;
}
