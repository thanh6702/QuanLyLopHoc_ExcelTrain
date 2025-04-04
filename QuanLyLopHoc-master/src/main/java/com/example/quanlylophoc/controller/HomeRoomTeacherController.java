package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Request.TeacherRequest;
import com.example.quanlylophoc.DTO.Request.TeacherValidator;
import com.example.quanlylophoc.DTO.Response.TeacherValidatorResult;
import com.example.quanlylophoc.Exception.AppException;
import com.example.quanlylophoc.entity.HomeRoomTeacherEntity;
import com.example.quanlylophoc.service.HomeRoomTeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teachers")
public class HomeRoomTeacherController {
    @Autowired
    private HomeRoomTeacherService homeRoomTeacherService;

    @PostMapping()
    public ResponseEntity<HomeRoomTeacherEntity> createTeacher(@Valid @RequestBody TeacherRequest teacherRequest) {
        HomeRoomTeacherEntity homeRoomTeacherEntity = homeRoomTeacherService.createTeacher(teacherRequest);
        return ResponseEntity.ok(homeRoomTeacherEntity);
    }

    @GetMapping()
    public ResponseEntity<List<HomeRoomTeacherEntity>> getAllTeachers() {
        List<HomeRoomTeacherEntity> homeRoomTeacherEntityList = homeRoomTeacherService.getAllTeachers();
        return ResponseEntity.ok(homeRoomTeacherEntityList);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<HomeRoomTeacherEntity>> searchTeachersByName(@PathVariable String name) {
        List<HomeRoomTeacherEntity> homeRoomTeacherEntityList = homeRoomTeacherService.searchTeacherByName(name);
        return ResponseEntity.ok(homeRoomTeacherEntityList);
    }

    @GetMapping("/searchCode/{code}")
    public ResponseEntity<List<HomeRoomTeacherEntity>> searchTeachersByCode(@PathVariable String code) {
        List<HomeRoomTeacherEntity> homeRoomTeacherEntityList = homeRoomTeacherService.searchTeacherByCode(code);
        return ResponseEntity.ok(homeRoomTeacherEntityList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HomeRoomTeacherEntity> updateTeacher(@Valid @PathVariable("id") Integer id, @RequestBody TeacherRequest teacherRequest) {
        HomeRoomTeacherEntity homeRoomTeacherEntity = homeRoomTeacherService.updateTeacher(id, teacherRequest);
        return ResponseEntity.ok(homeRoomTeacherEntity);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTeacher(@PathVariable int id) {
        homeRoomTeacherService.deleteTeacher(id);
        return ResponseEntity.ok("Delete successful: " + id);
    }

    // Export teachers to Excel


    @GetMapping("/export-template")
    public ResponseEntity<byte[]> exportTemplate() throws IOException {
        // Xử lý file được gửi qua form-data
        byte[] errorsFile = homeRoomTeacherService.exportTeachersToExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=teachers_with_errors.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(errorsFile);
    }

@PostMapping("/import")
public ResponseEntity<?> importTeachers(@RequestParam("file") MultipartFile file) {
    try {
        // Gọi service để xử lý import và nhận danh sách lỗi
        List<String> errorMessages = homeRoomTeacherService.importTeachersFromExcel(file);

        // Nếu có lỗi, trả về danh sách lỗi với mã 400
        if (!errorMessages.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages); // Trả về lỗi cho FE
        }

        // Nếu không có lỗi, trả về thông báo thành công
        return ResponseEntity.ok("Import successful");
    } catch (AppException e) {
        // Xử lý lỗi custom AppException
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (IOException e) {
        // Xử lý lỗi đọc file
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading file");
    } catch (Exception e) {
        // Xử lý các lỗi khác
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
    }
}

    @PostMapping("/export-error-records")
    public ResponseEntity<byte[]> exportErrorRecords(@RequestParam("file") MultipartFile file) {
        try {
            // Thực hiện validate dữ liệu từ file upload
            List<TeacherRequest> teacherRequests = homeRoomTeacherService.parseTeachersFromExcel(file); // Phương thức đọc file và map thành TeacherRequest
            List<TeacherValidatorResult> validationResults = TeacherValidator.validateTeachers(teacherRequests);

            // Lọc ra các bản ghi lỗi
            List<TeacherValidatorResult> errorRecords = validationResults.stream()
                    .filter(TeacherValidatorResult::hasErrors)
                    .collect(Collectors.toList());

            if (errorRecords.isEmpty()) {
                return ResponseEntity.ok().body("No error records found".getBytes());
            }

            // Tạo file Excel chứa các bản ghi lỗi
            byte[] errorFile = homeRoomTeacherService.generateErrorExcel(validationResults);

            // Trả về file Excel dưới dạng byte[]
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "error-records.xlsx");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return ResponseEntity.ok().headers(headers).body(errorFile);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading file".getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred".getBytes());
        }
    }




}
