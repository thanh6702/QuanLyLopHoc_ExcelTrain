package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Request.SubjectGroupDTO;
import com.example.quanlylophoc.DTO.Response.APIResponse;
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
    public ResponseEntity<APIResponse<SubjectGroup>> create(@RequestBody SubjectGroupDTO dto) {
        SubjectGroup created = subjectGroupService.create(dto);
        return ResponseEntity.ok(APIResponse.success(created));
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<SubjectGroup>>> getAll() {
        List<SubjectGroup> groups = subjectGroupService.getAll();
        return ResponseEntity.ok(APIResponse.success(groups));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<SubjectGroup>> getById(@PathVariable Long id) {
        SubjectGroup group = subjectGroupService.getById(id);
        return ResponseEntity.ok(APIResponse.success(group));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<SubjectGroup>> update(@PathVariable Long id, @RequestBody SubjectGroupDTO dto) {
        SubjectGroup updated = subjectGroupService.update(id, dto);
        return ResponseEntity.ok(APIResponse.success(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> delete(@PathVariable Long id) {
        subjectGroupService.delete(id);
        return ResponseEntity.ok(APIResponse.success("Deleted successfully"));
    }
}
