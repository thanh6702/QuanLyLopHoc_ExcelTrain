package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Request.SubjectDTO;
import com.example.quanlylophoc.DTO.Response.APIResponse;
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
    public ResponseEntity<APIResponse<Subject>> create(@RequestBody SubjectDTO dto) {
        Subject created = subjectService.create(dto);
        return ResponseEntity.ok(APIResponse.success(created));
    }

    @GetMapping("/{id}/teachers")
    public ResponseEntity<APIResponse<List<Teacher>>> getTeachersBySubjectId(@PathVariable Long id) {
        List<Teacher> teachers = subjectService.getTeachersBySubjectId(id);
        return ResponseEntity.ok(APIResponse.success(teachers));
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<Subject>>> getAll() {
        List<Subject> subjects = subjectService.getAll();
        return ResponseEntity.ok(APIResponse.success(subjects));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Subject>> getById(@PathVariable Long id) {
        Subject subject = subjectService.getById(id);
        return ResponseEntity.ok(APIResponse.success(subject));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<Subject>> update(@PathVariable Long id, @RequestBody SubjectDTO dto) {
        Subject updated = subjectService.update(id, dto);
        return ResponseEntity.ok(APIResponse.success(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.ok(APIResponse.success("Deleted successfully"));
    }
}
