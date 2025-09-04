package com.jobPortal.Util;

public class AuthHelper {
    public static boolean isBCryptEncoded(String password) {
        if (password == null) return false;
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }

}
