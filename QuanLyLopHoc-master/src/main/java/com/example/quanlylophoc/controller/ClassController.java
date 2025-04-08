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

    // Lấy tất cả lớp học
    @GetMapping
    public ResponseEntity<List<ClassEntity>> getAllClasses() {
        return ResponseEntity.ok(classService.getAllClasses());
    }

    // Lấy thông tin lớp học theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ClassEntity> getClassById(@PathVariable int id) {
        return classService.getClassById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // Tìm lớp học theo code
    @GetMapping("/code/{code}")
    public ResponseEntity<ClassEntity> getClassByCode(@PathVariable String code) {
        return classService.getClassByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Tìm lớp học theo từ khóa trong tên
    @GetMapping("/search")
    public ResponseEntity<List<ClassEntity>> searchClassesByName(@RequestParam String name) {
        return ResponseEntity.ok(classService.searchClassesByName(name));
    }

    // Lấy danh sách lớp học trong khoảng ngày tạo
    @GetMapping("/created-between")
    public ResponseEntity<List<ClassEntity>> getClassesCreatedBetween(
            @RequestParam("startDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,

            @RequestParam("endDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return ResponseEntity.ok(classService.getClassesCreatedBetween(startDate, endDate));
    }

    // Tạo hoặc cập nhật lớp học
    @PostMapping
    public ResponseEntity<ClassEntity> createOrUpdateClass(@RequestBody ClassEntity classEntity) {
        ClassEntity savedClass = classService.saveClass(classEntity);
        return ResponseEntity.ok(savedClass);
    }

    // Xóa lớp học theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassById(@PathVariable int id) {
        classService.deleteClassById(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/class/search")
    public ResponseEntity<PagedClassResponse> searchClass(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(classService.getAllClassWithSearchPaging(keyword, page, size));
    }

    @GetMapping("/class")
    public ResponseEntity<List<ClassResponse>> getAllClassPaging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(classService.getAllClassWithPaging(page, size));
    }

    @GetMapping("/export-template")
    public ResponseEntity<byte[]> exportTemplate() throws IOException {
        // Xử lý file được gửi qua form-data
        byte[] errorsFile = classService.exportClassToExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=class_with_errors.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(errorsFile);
    }

    @PostMapping("/export-error-records")
    public ResponseEntity<byte[]> exportErrorRecords(@RequestParam("file") MultipartFile file) {
        try {
            // Thực hiện validate dữ liệu từ file upload
            List<ClassRequest> classRequests = classService.parseTeachersFromExcel(file); // Phương thức đọc file và map thành TeacherRequest
            List<ClassValidatorResult> validationResults = ClassValidator.validateClass(classRequests);

            // Lọc ra các bản ghi lỗi
            List<ClassValidatorResult> errorRecords = validationResults.stream()
                    .filter(ClassValidatorResult::hasErrors)
                    .collect(Collectors.toList());

            if (errorRecords.isEmpty()) {
                return ResponseEntity.ok().body("No error records found".getBytes());
            }

            // Tạo file Excel chứa các bản ghi lỗi
            byte[] errorFile = classService.generateErrorExcel(validationResults);

            // Trả về file Excel dưới dạng byte[]
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "errorClass-records.xlsx");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return ResponseEntity.ok().headers(headers).body(errorFile);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading file".getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred".getBytes());
        }
    }


    // API Import Classes
    @PostMapping("/import")
    public ResponseEntity<?> importClasses(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "No file provided"));
        }

        try {
            // Gọi service để import dữ liệu từ file Excel
            Map<String, Object> importResult = classService.importClassesFromExcel(new ByteArrayInputStream(file.getBytes()));

            // Kiểm tra nếu có lỗi (skippedClasses)
            List<String> skippedClasses = (List<String>) importResult.get("skippedClasses");
            if (!skippedClasses.isEmpty()) {
                // Nếu có lỗi, tạo file chứa lỗi và trả về cho người dùng
                ByteArrayInputStream errorFile = classService.exportErrorFile(skippedClasses);

                // Tạo header HTTP cho file lỗi
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDisposition(ContentDisposition.attachment()
                        .filename("error-classes.xlsx")
                        .build());

                byte[] errorData = errorFile.readAllBytes();

                // Trả về file lỗi dưới dạng byte[]
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .headers(headers)
                        .body(errorData); // Trả về mảng byte chứa file lỗi
            }

            // Trả về kết quả thành công nếu không có lỗi
            return ResponseEntity.ok().body(Map.of(
                    "status", "success",
                    "importedClasses", importResult.get("importedClasses")
            ));

        } catch (IOException e) {
            // Xử lý lỗi khi đọc file
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "File import failed: " + e.getMessage()
            ));
        }
    }



}
