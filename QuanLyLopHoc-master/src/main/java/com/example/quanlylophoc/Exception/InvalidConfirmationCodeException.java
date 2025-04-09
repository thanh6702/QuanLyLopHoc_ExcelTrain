package com.example.quanlylophoc.Exception;

import lombok.Getter;

@Getter
public class InvalidConfirmationCodeException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidConfirmationCodeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
