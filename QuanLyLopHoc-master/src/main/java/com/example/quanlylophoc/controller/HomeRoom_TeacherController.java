package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Request.TeacherRequest;
import com.example.quanlylophoc.entity.HomeRoomTeacherEntity;
import com.example.quanlylophoc.service.HomeRoom_TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teachers")
public class HomeRoom_TeacherController {
    @Autowired
    private HomeRoom_TeacherService homeRoomTeacherService;

    @PostMapping()
    public ResponseEntity<HomeRoomTeacherEntity> createClass(@Valid  @RequestBody TeacherRequest teacherRequest) {
        HomeRoomTeacherEntity homeRoomTeacherEntity = homeRoomTeacherService.createTeacher(teacherRequest);
        return ResponseEntity.ok(homeRoomTeacherEntity);
    }

    @GetMapping()
    public ResponseEntity<List<HomeRoomTeacherEntity>> getAllClasses() {
        List<HomeRoomTeacherEntity> homeRoomTeacherEntity = homeRoomTeacherService.getAllTeachers();
        return ResponseEntity.ok(homeRoomTeacherEntity);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<HomeRoomTeacherEntity>> searchClassesByName(@PathVariable String name) {
        List<HomeRoomTeacherEntity> homeRoomTeacherEntity = homeRoomTeacherService.searchTeacherByName(name);
        return ResponseEntity.ok(homeRoomTeacherEntity);
    }
    @GetMapping("/searchCode/{code}")
    public ResponseEntity<List<HomeRoomTeacherEntity>> searchClassesByCode(@PathVariable String code) {
        List<HomeRoomTeacherEntity> homeRoomTeacherEntity = homeRoomTeacherService.searchTeacherByCode(code);
        return ResponseEntity.ok(homeRoomTeacherEntity);
    }
    @PutMapping("/{id}")
    public ResponseEntity<HomeRoomTeacherEntity> updateClass(@Valid @PathVariable("id") Integer id, @RequestBody TeacherRequest teacherRequest) {
        HomeRoomTeacherEntity homeRoomTeacherEntity = homeRoomTeacherService.updateTeacher(id, teacherRequest);
        return ResponseEntity.ok(homeRoomTeacherEntity);
    }

    @DeleteMapping("/delete/{id}")
    String deleteClass(@PathVariable int id) {
        homeRoomTeacherService.deleteTeacher(id);
        return "delete successfull " + id;
    }

    // Export teachers to Excel
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportTeachersToExcel() {
        ByteArrayInputStream byteArrayInputStream = homeRoomTeacherService.exportTeachersToExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=teachers.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(byteArrayInputStream.readAllBytes());
    }

    // Import teachers from Excel
    @PostMapping("/import")
    public ResponseEntity<?> importTeachersFromExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "No file uploaded"
            ));
        }

        try {
            // Gọi phương thức service để import giáo viên
            List<HomeRoomTeacherEntity> importedTeachers = homeRoomTeacherService.importTeachersFromExcel(
                    new ByteArrayInputStream(file.getBytes())
            );

            // Trả về phản hồi kèm danh sách giáo viên
            return ResponseEntity.ok(Map.of(
                    "message", "Imported " + importedTeachers.size() + " teachers",
                    "importedTeachers", importedTeachers
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Failed to import teachers",
                    "error", e.getMessage()
            ));
        }
    }




}
