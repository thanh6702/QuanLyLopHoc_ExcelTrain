package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Request.ClassRequest;
import com.example.quanlylophoc.Exception.AppException;
import com.example.quanlylophoc.entity.ClassEntity;
import com.example.quanlylophoc.service.ClassService;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/class")
public class ClassController {
    @Autowired
    private ClassService classService;

    @PostMapping()
    public ResponseEntity<ClassEntity> createClass(@Valid @RequestBody ClassRequest classRequest) {
        ClassEntity classEntity = classService.createClass(classRequest);
        return ResponseEntity.ok(classEntity);
    }

    @GetMapping()
    public ResponseEntity<List<ClassEntity>> getAllClasses() {
        List<ClassEntity> classEntityList = classService.getListClasses();
        return ResponseEntity.ok(classEntityList);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<ClassEntity>> searchClassesByName(@PathVariable String name) {
        List<ClassEntity> classEntityList = classService.searchClassesByName(name);
        return ResponseEntity.ok(classEntityList);
    }
    @GetMapping("/searchCode/{code}")
    public ResponseEntity<List<ClassEntity>> searchClassesByCode(@PathVariable String code) {
        List<ClassEntity> classEntityList = classService.searchClassesByCode(code);
        return ResponseEntity.ok(classEntityList);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ClassEntity> updateClass(@Valid @PathVariable("id") Integer id,@RequestBody ClassRequest classRequest) {
        ClassEntity classEntity = classService.updateClass(id, classRequest);
        return ResponseEntity.ok(classEntity);
    }


@DeleteMapping("/delete/{classId}")
public ResponseEntity<String> deleteClass(@PathVariable int classId) {
    try {
        classService.deleteClassIfEmpty(classId); // Xóa lớp nếu lớp không có học sinh
        return ResponseEntity.ok("Lớp đã được xóa.");
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}


    // API Export Classes
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportClasses() {
        ByteArrayInputStream excelData = classService.exportClassesToExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=classes.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData.readAllBytes());
    }
    @GetMapping("/exports")
    public ResponseEntity<byte[]> exportClassesWithTemplate() {
        try {
            ByteArrayInputStream byteArrayInputStream = classService.exportClassesWithTemplate();

            // Tạo header HTTP cho file
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("template-class.xlsx")
                    .build());

            // Đọc dữ liệu từ ByteArrayInputStream
            byte[] data = byteArrayInputStream.readAllBytes();

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);

        } catch (AppException e) {
            // Xử lý lỗi nếu có
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error during export: " + e.getMessage()).getBytes());
        }
    }

    // API Import Classes
    @PostMapping("/import")
    public ResponseEntity<Void> importClasses(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            classService.importClassesFromExcel(new ByteArrayInputStream(file.getBytes()));
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

    }


}
