package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Request.ClassRequest;
import com.example.quanlylophoc.DTO.Request.ClassValidator;
import com.example.quanlylophoc.DTO.Request.TeacherRequest;
import com.example.quanlylophoc.DTO.Request.TeacherValidator;
import com.example.quanlylophoc.DTO.Response.*;
import com.example.quanlylophoc.Exception.AppException;
import com.example.quanlylophoc.entity.ClassEntity;
import com.example.quanlylophoc.service.ClassService;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/class")
public class ClassController {
    @Autowired
    private ClassService classService;

    @GetMapping
    public ResponseEntity<APIResponse<List<ClassEntity>>> getAllClasses() {
        return ResponseEntity.ok(APIResponse.success(classService.getAllClasses()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<ClassEntity>> getClassById(@PathVariable int id) {
        return classService.getClassById(id)
                .map(classEntity -> ResponseEntity.ok(APIResponse.success(classEntity)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(APIResponse.error(404, "NOT_FOUND", "Lớp học không tồn tại")));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<APIResponse<ClassEntity>> getClassByCode(@PathVariable String code) {
        return classService.getClassByCode(code)
                .map(classEntity -> ResponseEntity.ok(APIResponse.success(classEntity)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(APIResponse.error(404, "NOT_FOUND", "Không tìm thấy lớp với mã code")));
    }

    @GetMapping("/search")
    public ResponseEntity<APIResponse<List<ClassEntity>>> searchClassesByName(@RequestParam String name) {
        return ResponseEntity.ok(APIResponse.success(classService.searchClassesByName(name)));
    }

    @GetMapping("/created-between")
    public ResponseEntity<APIResponse<List<ClassEntity>>> getClassesCreatedBetween(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return ResponseEntity.ok(APIResponse.success(classService.getClassesCreatedBetween(startDate, endDate)));
    }

    @PostMapping
    public ResponseEntity<APIResponse<ClassEntity>> createOrUpdateClass(@RequestBody ClassEntity classEntity) {
        return ResponseEntity.ok(APIResponse.success(classService.saveClass(classEntity)));
    }

    @GetMapping("/class/with-subjects")
    public ResponseEntity<APIResponse<PagedClassResponse>> getClassWithSubjectsPaging(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(APIResponse.success(classService.getClassWithSubjectsPaging(keyword, page, size)));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteClassById(@PathVariable int id) {
        classService.deleteClassById(id);
        return ResponseEntity.ok(APIResponse.success("Xóa lớp học thành công"));
    }

    @GetMapping("/class/search")
    public ResponseEntity<APIResponse<PagedClassResponse>> searchClass(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(APIResponse.success(classService.getAllClassWithSearchPaging(keyword, page, size)));
    }

    @GetMapping("/class")
    public ResponseEntity<APIResponse<List<ClassResponse>>> getAllClassPaging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(APIResponse.success(classService.getAllClassWithPaging(page, size)));
    }

    @GetMapping("/export-template")
    public ResponseEntity<byte[]> exportTemplate() throws IOException {
        byte[] errorsFile = classService.exportClassToExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=class_with_errors.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(errorsFile);
    }

    @PostMapping("/export-error-records")
    public ResponseEntity<?> exportErrorRecords(@RequestParam("file") MultipartFile file) {
        try {
            List<ClassRequest> classRequests = classService.parseTeachersFromExcel(file);
            List<ClassValidatorResult> validationResults = ClassValidator.validateClass(classRequests);

            List<ClassValidatorResult> errorRecords = validationResults.stream()
                    .filter(ClassValidatorResult::hasErrors)
                    .collect(Collectors.toList());

            if (errorRecords.isEmpty()) {
                return ResponseEntity.ok(APIResponse.success("No error records found"));
            }

            byte[] errorFile = classService.generateErrorExcel(validationResults);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "errorClass-records.xlsx");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            return ResponseEntity.ok().headers(headers).body(errorFile);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.error("Error reading file"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.error("Unexpected error occurred"));
        }
    }

    @PostMapping("/import")
    public ResponseEntity<?> importClasses(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(APIResponse.error(400, "NO_FILE", "No file provided"));
        }

        try {
            Map<String, Object> importResult = classService.importClassesFromExcel(new ByteArrayInputStream(file.getBytes()));

            List<String> skippedClasses = (List<String>) importResult.get("skippedClasses");
            if (!skippedClasses.isEmpty()) {
                ByteArrayInputStream errorFile = classService.exportErrorFile(skippedClasses);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDisposition(ContentDisposition.attachment().filename("error-classes.xlsx").build());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .headers(headers)
                        .body(errorFile.readAllBytes());
            }

            return ResponseEntity.ok(APIResponse.success(importResult.get("importedClasses")));

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(APIResponse.error(500, "IMPORT_FAILED", "File import failed: " + e.getMessage()));
        }
    }
}


