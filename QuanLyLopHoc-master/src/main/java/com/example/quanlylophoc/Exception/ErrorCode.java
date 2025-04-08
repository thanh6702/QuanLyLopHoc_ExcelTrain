package com.example.quanlylophoc.Exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    CLASSNAME_EXISTED(HttpStatus.BAD_REQUEST, "Class Name is already existed"),
    CODE_EXISTED(HttpStatus.BAD_REQUEST, "Code already existed"),
    KEY_ERROR_INVALID(HttpStatus.BAD_REQUEST, "Message Key is invalid"),
    NAME_INVALID(HttpStatus.BAD_REQUEST, "Name is illegal"),
    ID_NOT_FOUND(HttpStatus.NOT_FOUND, "ID not found"),
    CANNOT_DELETE_CLASS(HttpStatus.BAD_REQUEST, "Cannot delete class"),
    CLASS_ID_INVALID(HttpStatus.BAD_REQUEST, "ClassID is invalid"),
    EXPORT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Export failed"),
    IMPORT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Import failed"),
    USERNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Username already exists"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "Invalid password"),
    TEACHER_ALREADY_EXISTS_FOR_CLASS(HttpStatus.BAD_REQUEST, "Teacher already exists for class");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
