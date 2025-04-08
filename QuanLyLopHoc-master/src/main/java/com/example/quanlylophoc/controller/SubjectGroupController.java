package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Request.SubjectGroupDTO;
import com.example.quanlylophoc.entity.SubjectGroup;
import com.example.quanlylophoc.service.SubjectGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subject-groups")
@RequiredArgsConstructor
public class SubjectGroupController {

    private final SubjectGroupService subjectGroupService;

    @PostMapping
    public ResponseEntity<SubjectGroup> create(@RequestBody SubjectGroupDTO dto) {
        return ResponseEntity.ok(subjectGroupService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<SubjectGroup>> getAll() {
        return ResponseEntity.ok(subjectGroupService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectGroup> getById(@PathVariable Long id) {
        return ResponseEntity.ok(subjectGroupService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectGroup> update(@PathVariable Long id, @RequestBody SubjectGroupDTO dto) {
        return ResponseEntity.ok(subjectGroupService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subjectGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
