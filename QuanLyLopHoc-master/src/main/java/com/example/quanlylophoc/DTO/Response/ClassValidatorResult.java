package com.example.quanlylophoc.DTO.Response;

import com.example.quanlylophoc.DTO.Request.ClassRequest;
import com.example.quanlylophoc.DTO.Request.TeacherRequest;

import java.util.ArrayList;
import java.util.List;

public class ClassValidatorResult {
    private ClassRequest classRequest;  // Thông tin giáo viên
    private List<String> errors;  // Danh sách lỗi

    public ClassValidatorResult() {
        this.errors = new ArrayList<>();
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public ClassRequest getClassRequest() {
        return classRequest;
    }

    public void setClassRequest(ClassRequest classRequest) {
        this.classRequest = classRequest;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addError(String error) {
        this.errors.add(error);  // Thêm lỗi vào danh sách
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();  // Kiểm tra nếu có lỗi
    }



}
