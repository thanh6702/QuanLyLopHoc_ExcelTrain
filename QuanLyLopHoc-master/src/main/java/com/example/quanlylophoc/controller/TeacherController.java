package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Response.APIResponse;
import com.example.quanlylophoc.entity.Teacher;
import com.example.quanlylophoc.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    public ResponseEntity<APIResponse<List<Teacher>>> getAll() {
        List<Teacher> teachers = teacherService.getAllTeachers();
        return ResponseEntity.ok(APIResponse.success(teachers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Teacher>> getById(@PathVariable Long id) {
        Teacher teacher = teacherService.getTeacherById(id);
        return ResponseEntity.ok(APIResponse.success(teacher));
    }
}
