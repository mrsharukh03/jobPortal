package com.jobPortal.DTO.AuthDTO;

public class MailSendResult {
    private boolean success;
    private String message;

    public MailSendResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}


