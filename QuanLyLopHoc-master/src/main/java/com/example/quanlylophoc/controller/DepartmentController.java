package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Request.DepartmentDTO;
import com.example.quanlylophoc.DTO.Request.DepartmentTreeDTO;
import com.example.quanlylophoc.entity.Departments;
import com.example.quanlylophoc.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<Departments> create(@RequestBody DepartmentDTO dto) {
        return ResponseEntity.ok(departmentService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<Departments>> getAll() {
        return ResponseEntity.ok(departmentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Departments> getById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Departments> update(@PathVariable Long id, @RequestBody DepartmentDTO dto) {
        return ResponseEntity.ok(departmentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getlist")
    public ResponseEntity<List<DepartmentTreeDTO>> getFullTree() {
        return ResponseEntity.ok(departmentService.getFullTree());
    }

}
