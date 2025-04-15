package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Request.DepartmentDTO;
import com.example.quanlylophoc.DTO.Request.DepartmentTreeDTO;
import com.example.quanlylophoc.DTO.Response.APIResponse;
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
    public ResponseEntity<APIResponse<Departments>> create(@RequestBody DepartmentDTO dto) {
        return ResponseEntity.ok(APIResponse.success(departmentService.create(dto)));
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<Departments>>> getAll() {
        return ResponseEntity.ok(APIResponse.success(departmentService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Departments>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(APIResponse.success(departmentService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<Departments>> update(@PathVariable Long id, @RequestBody DepartmentDTO dto) {
        return ResponseEntity.ok(APIResponse.success(departmentService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.ok(APIResponse.success("Xóa thành công" + id));
    }

    @GetMapping("/tree")
    public ResponseEntity<APIResponse<List<DepartmentTreeDTO>>> getFullTree() {
        return ResponseEntity.ok(APIResponse.success(departmentService.getFullTree()));
    }

}
