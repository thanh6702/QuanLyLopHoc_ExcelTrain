package com.example.quanlylophoc.Exception;

import com.example.quanlylophoc.DTO.Response.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandling {

    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<APIResponse> handleRuntimeException(RuntimeException exception) {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setCode(500); //setCode response khi gặp lỗi
        apiResponse.setMessage(exception.getMessage()); //setmessage, lấy message được tạo
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<APIResponse> handleAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        APIResponse apiResponse = new APIResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<APIResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.KEY_ERROR_INVALID;
        try {
            errorCode = ErrorCode.valueOf(enumKey); //lấy Key để báo message được khai báo trong ErrorCode
        }catch (IllegalArgumentException e){

        }

        APIResponse apiResponse = new APIResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

}
