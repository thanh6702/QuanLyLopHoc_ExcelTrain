package com.example.quanlylophoc.DTO.Response;

import com.example.quanlylophoc.DTO.Request.TeacherRequest;

import java.util.*;

public class TeacherValidatorResult {
    private TeacherRequest teacher;  // Thông tin giáo viên
    private List<String> errors;  // Danh sách lỗi

    public TeacherValidatorResult() {
        this.errors = new ArrayList<>();
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    // Getter và Setter cho teacher và errors
    public TeacherRequest getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherRequest teacher) {
        this.teacher = teacher;
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
