package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Request.StudentRequest;
import com.example.quanlylophoc.DTO.Response.*;
import com.example.quanlylophoc.entity.StudentEntity;
import com.example.quanlylophoc.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<APIResponse<StudentEntity>> createStudent(@Valid @RequestBody StudentRequest studentRequest) {
        StudentEntity studentEntity = studentService.createStudent(studentRequest);
        return ResponseEntity.ok(APIResponse.success(studentEntity));
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<StudentEntity>>> getAllStudents() {
        List<StudentEntity> studentEntities = studentService.getAllStudents();
        return ResponseEntity.ok(APIResponse.success(studentEntities));
    }

    @GetMapping("/class/search")
    public ResponseEntity<APIResponse<PagedStudentResponse>> searchClass(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PagedStudentResponse response = studentService.getAllStudentsWithSearchPaging(keyword, page, size);
        return ResponseEntity.ok(APIResponse.success(response));
    }

    @GetMapping("/studentsList")
    public ResponseEntity<APIResponse<List<StudentInfoResponse>>> getAllUsersPaging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        List<StudentInfoResponse> result = studentService.getAllStudentsWithPaging(page, size);
        return ResponseEntity.ok(APIResponse.success(result));
    }

    @GetMapping("/search/name/{name}")
    public ResponseEntity<APIResponse<List<StudentEntity>>> searchStudentsByName(@PathVariable String name) {
        List<StudentEntity> result = studentService.searchStudentByName(name);
        return ResponseEntity.ok(APIResponse.success(result));
    }

    @GetMapping("/search/code/{code}")
    public ResponseEntity<APIResponse<List<StudentEntity>>> searchStudentsByCode(@PathVariable String code) {
        List<StudentEntity> result = studentService.searchStudentByCode(code);
        return ResponseEntity.ok(APIResponse.success(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<StudentEntity>> updateStudent(@PathVariable("id") Integer id, @Valid @RequestBody StudentRequest studentRequest) {
        StudentEntity updatedStudent = studentService.updateStudent(id, studentRequest);
        return ResponseEntity.ok(APIResponse.success(updatedStudent));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteStudent(@PathVariable("id") Integer id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(APIResponse.success("Delete successful for ID: " + id));
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
    public ResponseEntity<APIResponse<String>> importStudentsFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            studentService.importStudentsFromExcel((ByteArrayInputStream) file.getInputStream());
            return ResponseEntity.ok(APIResponse.success("Students imported successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.error(500, "IMPORT_FAILED", "Failed to import students"));
        }
    }
}
