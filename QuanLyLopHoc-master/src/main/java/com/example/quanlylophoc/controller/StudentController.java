package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Request.StudentRequest;
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

    @PostMapping()
    public ResponseEntity<StudentEntity> createClass(@Valid @RequestBody StudentRequest studentRequest) {
        StudentEntity studentEntity = studentService.createStudent(studentRequest);
        return ResponseEntity.ok(studentEntity);
    }

    @GetMapping()
    public ResponseEntity<List<StudentEntity>> getAllClasses() {
        List<StudentEntity> studentEntity = studentService.getAllStudents();
        return ResponseEntity.ok(studentEntity);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<StudentEntity>> searchClassesByName(@PathVariable String name) {
        List<StudentEntity> studentEntity = studentService.searchStudentByName(name);
        return ResponseEntity.ok(studentEntity);
    }
    @GetMapping("/searchCode/{code}")
    public ResponseEntity<List<StudentEntity>> searchClassesByCode(@PathVariable String code) {
        List<StudentEntity> studentEntity = studentService.searchStudentByCode(code);
        return ResponseEntity.ok(studentEntity);
    }
    @PutMapping("/{id}")
    public ResponseEntity<StudentEntity> updateClass(@Valid @PathVariable("id") Integer id,@RequestBody StudentRequest studentRequest) {
        StudentEntity studentEntity = studentService.updateStudent(id, studentRequest);
        return ResponseEntity.ok(studentEntity);
    }

    @DeleteMapping("/delete/{id}")
    String deleteClass(@PathVariable int id) {
        studentService.deleteStudent(id);
        return "delete successfull" + id;
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
