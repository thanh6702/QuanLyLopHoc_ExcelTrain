package com.example.quanlylophoc.DTO.Response;


import com.example.quanlylophoc.Exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) //khai báo những message nào null thì không hiển thị
public class APIResponse <T>{
    private int status;
    private String errorCode;
    private String message;

    public APIResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus().value();
        this.errorCode = errorCode.name();
        this.message = errorCode.getMessage();
    }

}