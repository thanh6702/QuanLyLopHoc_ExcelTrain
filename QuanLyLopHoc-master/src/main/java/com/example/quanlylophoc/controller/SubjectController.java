package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Request.SubjectDTO;
import com.example.quanlylophoc.entity.Subject;
import com.example.quanlylophoc.entity.Teacher;
import com.example.quanlylophoc.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    public ResponseEntity<Subject> create(@RequestBody SubjectDTO dto) {
        return ResponseEntity.ok(subjectService.create(dto));
    }
    @GetMapping("/{id}/teachers")
    public ResponseEntity<List<Teacher>> getTeachersBySubjectId(@PathVariable Long id) {
        List<Teacher> teachers = subjectService.getTeachersBySubjectId(id);
        return ResponseEntity.ok(teachers);
    }
    @GetMapping
    public ResponseEntity<List<Subject>> getAll() {
        return ResponseEntity.ok(subjectService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subject> getById(@PathVariable Long id) {
        return ResponseEntity.ok(subjectService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subject> update(@PathVariable Long id, @RequestBody SubjectDTO dto) {
        return ResponseEntity.ok(subjectService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
