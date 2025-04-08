package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Request.StudentRequest;
import com.example.quanlylophoc.DTO.Response.PagedClassResponse;
import com.example.quanlylophoc.DTO.Response.PagedStudentResponse;
import com.example.quanlylophoc.DTO.Response.StudentInfoResponse;
import com.example.quanlylophoc.DTO.Response.UserInfoResponse;
import com.example.quanlylophoc.entity.StudentEntity;
import com.example.quanlylophoc.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentEntity> createStudent(@Valid @RequestBody StudentRequest studentRequest) {
        StudentEntity studentEntity = studentService.createStudent(studentRequest);
        return ResponseEntity.ok(studentEntity);
    }

    @GetMapping
    public ResponseEntity<List<StudentEntity>> getAllStudents() {
        List<StudentEntity> studentEntities = studentService.getAllStudents();
        return ResponseEntity.ok(studentEntities);
    }

    @GetMapping("/class/search")
    public ResponseEntity<PagedStudentResponse> searchClass(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(studentService.getAllStudentsWithSearchPaging(keyword, page, size));
    }

    @GetMapping("/studentsList")
    public ResponseEntity<List<StudentInfoResponse>> getAllUsersPaging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(studentService.getAllStudentsWithPaging(page, size));
    }

    @GetMapping("/search/name/{name}")
    public ResponseEntity<List<StudentEntity>> searchStudentsByName(@PathVariable String name) {
        List<StudentEntity> studentEntities = studentService.searchStudentByName(name);
        return ResponseEntity.ok(studentEntities);
    }

    @GetMapping("/search/code/{code}")
    public ResponseEntity<List<StudentEntity>> searchStudentsByCode(@PathVariable String code) {
        List<StudentEntity> studentEntities = studentService.searchStudentByCode(code);
        return ResponseEntity.ok(studentEntities);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentEntity> updateStudent(@PathVariable("id") Integer id, @Valid @RequestBody StudentRequest studentRequest) {
        StudentEntity studentEntity = studentService.updateStudent(id, studentRequest);
        return ResponseEntity.ok(studentEntity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable("id") Integer id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok("Delete successful for ID: " + id);
    }
    // Export students to Excel
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportStudentsToExcel() {
        ByteArrayInputStream byteArrayInputStream = studentService.exportStudentsToExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(byteArrayInputStream.readAllBytes());
    }

    // Import students from Excel
    @PostMapping("/import")
    public ResponseEntity<String> importStudentsFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            studentService.importStudentsFromExcel((ByteArrayInputStream) file.getInputStream());
            return ResponseEntity.status(HttpStatus.OK).body("Students imported successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to import students");
        }
    }


}
