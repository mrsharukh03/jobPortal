package com.jobPortal.Security;


import lombok.Data;

import java.util.List;
import java.util.UUID;
@Data
public class JwtUserPrincipal {

    private final UUID userId;
    private final String email;
    private final List<String> roles;

}
