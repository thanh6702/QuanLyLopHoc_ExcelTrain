package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Request.TeacherRequest;
import com.example.quanlylophoc.DTO.Request.TeacherValidator;
import com.example.quanlylophoc.DTO.Response.APIResponse;
import com.example.quanlylophoc.DTO.Response.TeacherValidatorResult;
import com.example.quanlylophoc.Exception.AppException;
import com.example.quanlylophoc.entity.HomeRoomTeacherEntity;
import com.example.quanlylophoc.service.HomeRoomTeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class HomeRoomTeacherController {

    private final HomeRoomTeacherService homeRoomTeacherService;

    @PostMapping
    public ResponseEntity<APIResponse<HomeRoomTeacherEntity>> createTeacher(@Valid @RequestBody TeacherRequest teacherRequest) {
        HomeRoomTeacherEntity created = homeRoomTeacherService.createTeacher(teacherRequest);
        return ResponseEntity.ok(APIResponse.success(created));
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<HomeRoomTeacherEntity>>> getAllTeachers() {
        List<HomeRoomTeacherEntity> teachers = homeRoomTeacherService.getAllTeachers();
        return ResponseEntity.ok(APIResponse.success(teachers));
    }

    @GetMapping("/search/name/{name}")
    public ResponseEntity<APIResponse<List<HomeRoomTeacherEntity>>> searchTeachersByName(@PathVariable String name) {
        List<HomeRoomTeacherEntity> results = homeRoomTeacherService.searchTeacherByName(name);
        return ResponseEntity.ok(APIResponse.success(results));
    }

    @GetMapping("/search/code/{code}")
    public ResponseEntity<APIResponse<List<HomeRoomTeacherEntity>>> searchTeachersByCode(@PathVariable String code) {
        List<HomeRoomTeacherEntity> results = homeRoomTeacherService.searchTeacherByCode(code);
        return ResponseEntity.ok(APIResponse.success(results));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<HomeRoomTeacherEntity>> updateTeacher(
            @PathVariable("id") Integer id,
            @Valid @RequestBody TeacherRequest teacherRequest
    ) {
        HomeRoomTeacherEntity updated = homeRoomTeacherService.updateTeacher(id, teacherRequest);
        return ResponseEntity.ok(APIResponse.success(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteTeacher(@PathVariable int id) {
        homeRoomTeacherService.deleteTeacher(id);
        return ResponseEntity.ok(APIResponse.success("Deleted teacher with id: " + id));
    }

    @GetMapping("/export-template")
    public ResponseEntity<byte[]> exportTemplate() throws IOException {
        byte[] fileData = homeRoomTeacherService.exportTeachersToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename("teachers_template.xlsx").build());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
    }

    @PostMapping("/import")
    public ResponseEntity<?> importTeachers(@RequestParam("file") MultipartFile file) {
        try {
            List<String> errorMessages = homeRoomTeacherService.importTeachersFromExcel(file);
            if (!errorMessages.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
            }
            return ResponseEntity.ok("Import successful");
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            // Xử lý lỗi đọc file
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading file");
        } catch (Exception e) {
            // Xử lý các lỗi khác
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PostMapping("/export-errors")
    public ResponseEntity<byte[]> exportErrorRecords(@RequestParam("file") MultipartFile file) {
        try {
            List<TeacherRequest> teacherRequests = homeRoomTeacherService.parseTeachersFromExcel(file);
            List<TeacherValidatorResult> validationResults = TeacherValidator.validateTeachers(teacherRequests);

            List<TeacherValidatorResult> errorRecords = validationResults.stream()
                    .filter(TeacherValidatorResult::hasErrors)
                    .collect(Collectors.toList());

            if (errorRecords.isEmpty()) {
                return ResponseEntity.ok("No error records found".getBytes());
            }

            byte[] errorFile = homeRoomTeacherService.generateErrorExcel(errorRecords);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment().filename("error-records.xlsx").build());
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(errorFile, headers, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading file".getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error occurred".getBytes());
        }
    }
}
