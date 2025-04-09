package com.example.quanlylophoc.DTO.Response;


import com.example.quanlylophoc.Exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse<T> {
    int status;
    String errorCode;
    String message;
    T data;

    public APIResponse() {
    }

    public APIResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus().value();
        this.errorCode = errorCode.name();
        this.message = errorCode.getMessage();
    }

    public APIResponse(ErrorCode errorCode, T data) {
        this(errorCode);
        this.data = data;
    }

    public APIResponse(int status, String errorCode, String message, T data) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
    }

    public static <T> APIResponse<T> success(T data) {
        return new APIResponse<>(200, null, "Success", data);
    }

    public static <T> APIResponse<T> error(int status, String errorCode, String message) {
        return new APIResponse<>(status, errorCode, message, null);
    }

    public static <T> APIResponse<T> fromErrorCode(ErrorCode errorCode) {
        return new APIResponse<>(errorCode);
    }
}