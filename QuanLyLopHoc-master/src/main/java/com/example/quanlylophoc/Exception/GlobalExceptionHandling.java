package com.example.quanlylophoc.Exception;

import com.example.quanlylophoc.DTO.Response.APIResponse;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandling {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<APIResponse> handleBadRequest(BadRequestException ex) {
        return ResponseEntity
                .status(ex.getErrorCode().getStatus())
                .body(new APIResponse(ex.getErrorCode()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<APIResponse> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity
                .status(ex.getErrorCode().getStatus())
                .body(new APIResponse(ex.getErrorCode()));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<APIResponse> handleInternal(InternalServerException ex) {
        return ResponseEntity
                .status(ex.getErrorCode().getStatus())
                .body(new APIResponse(ex.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse> handleUnknown(Exception ex) {
        return ResponseEntity
                .status(ErrorCode.INTERNAL_ERROR.getStatus())
                .body(new APIResponse(ErrorCode.INTERNAL_ERROR));
    }
    @ExceptionHandler(ConfirmationRequiredException.class)
    public ResponseEntity<APIResponse> handleConfirmationRequired(ConfirmationRequiredException ex) {
        Map<String, Object> data = new HashMap<>();
        data.put("confirmationCode", ex.getConfirmationCode());
        data.put("action", ex.getAction());

        APIResponse response = new APIResponse(
                HttpStatus.OK.value(),
                ErrorCode.CONFIRMATION_REQUIRED.name(),
                ex.getMessage(),
                data
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @ExceptionHandler(InvalidConfirmationCodeException.class)
    public ResponseEntity<APIResponse> handleInvalidConfirmationCode(InvalidConfirmationCodeException ex) {
        return ResponseEntity
                .status(ex.getErrorCode().getStatus())
                .body(new APIResponse(ex.getErrorCode()));
    }


//    @ExceptionHandler(AppException.class)
//    public ResponseEntity<APIResponse> handleAppException(AppException ex) {
//        return ResponseEntity
//                .status(ex.getErrorCode().getStatus())
//                .body(new APIResponse(ex.getMessage()));
//    }

}
