package com.example.quanlylophoc.Exception;

import lombok.Getter;

@Getter
public class ConfirmationRequiredException extends RuntimeException {
    private final String action;
    private final String confirmationCode;

    public ConfirmationRequiredException(String message, String action, String confirmationCode) {
        super(message);
        this.action = action;
        this.confirmationCode = confirmationCode;
    }
}
