package com.example.quanlylophoc.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


public enum ErrorCode {
    CLASSNAME_EXISTED (1001, "Class Name is already existed", HttpStatus.NOT_FOUND),
    CODE_EXISTED (1111, "Code already existed ", HttpStatus.BAD_REQUEST),
    KEY_ERROR_INVALID (1113, "Message Key is invalid", HttpStatus.BAD_REQUEST),
    NAME_INVALID (1002, "Name is illegal", HttpStatus.BAD_REQUEST),
    ID_NOT_FOUND (1003, "ID not found", HttpStatus.NOT_FOUND),
    CANNOT_DELETE_CLASS(1004,"Cannot delete class", HttpStatus.BAD_REQUEST),
    CLASS_ID_INVALID (1005, "ClassID is invalid", HttpStatus.BAD_REQUEST),
    EXPORT_FAILED(1113,"Export failed", HttpStatus.INTERNAL_SERVER_ERROR),
    IMPORT_FAILED(1115,"Import failed", HttpStatus.INTERNAL_SERVER_ERROR),
    TEACHER_ALREADY_EXISTS_FOR_CLASS(1006,"Teacher Already Exists For Class" , HttpStatus.BAD_REQUEST ),;
    private int code;
    private String message;
    private HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
