package com.example.quanlylophoc.DTO.Response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) //khai báo những message nào null thì không hiển thị
public class APIResponse <T>{
    @Builder.Default
    private int code = 1000;
    private String message;
    private T result;

    public APIResponse(int code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public APIResponse() {

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

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}