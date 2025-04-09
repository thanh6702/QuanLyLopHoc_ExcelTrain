package com.example.quanlylophoc.service;

import com.example.quanlylophoc.entity.Teacher;
import com.example.quanlylophoc.repository.TeacherRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TeacherService {
    private final TeacherRepository teacherRepository;

    public Teacher getTeacherById(Long teacherId) {
        return teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + teacherId));
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }
}
