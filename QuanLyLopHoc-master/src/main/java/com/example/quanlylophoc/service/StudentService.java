package com.example.quanlylophoc.service;

import com.example.quanlylophoc.DTO.Request.ClassRequest;
import com.example.quanlylophoc.DTO.Request.StudentRequest;
import com.example.quanlylophoc.DTO.Response.PagedStudentResponse;
import com.example.quanlylophoc.DTO.Response.PagedUserResponse;
import com.example.quanlylophoc.DTO.Response.StudentInfoResponse;
import com.example.quanlylophoc.DTO.Response.UserInfoResponse;
import com.example.quanlylophoc.Exception.AppException;
import com.example.quanlylophoc.Exception.ErrorCode;
import com.example.quanlylophoc.entity.ClassEntity;
import com.example.quanlylophoc.entity.StudentEntity;
import com.example.quanlylophoc.entity.UserEntity;
import com.example.quanlylophoc.repository.ClassRepository;
import com.example.quanlylophoc.repository.StudentRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    public StudentEntity createStudent(StudentRequest studentRequest) {
        validateStudentRequest(studentRequest, false);
        StudentEntity studentEntity = new StudentEntity();
        mapRequestToEntity(studentRequest, studentEntity);
        studentEntity.setCreateDate(new Date());
        return studentRepository.save(studentEntity);
    }

    public List<StudentEntity> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<StudentInfoResponse> getAllStudentsWithPaging(int page, int size) {
        int offset = page * size;
        List<StudentEntity> users = studentRepository.findAllUsersWithPagination(size, offset);

        return users.stream()
                .map(user -> StudentInfoResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .code(user.getCode())
                        .classId(user.getClassId())
                        .createDate(user.getCreateDate())
                        .updateDate(user.getUpdateDate())
                        .build())
                .collect(Collectors.toList());
    }

    public PagedStudentResponse getAllStudentsWithSearchPaging(String keyword, int page, int size) {
        int offset = page * size;

        List<StudentEntity> studentEntities = studentRepository
                .searchStudentsWithPagination(keyword, size, offset);

        int total = studentRepository.countSearchStudents(keyword);

        List<StudentInfoResponse> users = studentEntities.stream()
                .map(user -> StudentInfoResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .code(user.getCode())
                        .classId(user.getClassId())
                        .createDate(user.getCreateDate())
                        .updateDate(user.getUpdateDate())
                        .build())
                .toList();

        return PagedStudentResponse.builder()
                .students(users)
                .total(total)
                .build();
    }

    public List<StudentEntity> searchStudentByName(String name) {
        return studentRepository.findByNameContainingIgnoreCase(name);
    }

    public List<StudentEntity> searchStudentByCode(String code) {
        return studentRepository.findByCodeContainingIgnoreCase(code);
    }

    public StudentEntity updateStudent(Integer id, StudentRequest studentRequest) {
        StudentEntity studentEntity = studentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ID_NOT_FOUND));
        validateStudentRequest(studentRequest, true);
        mapRequestToEntity(studentRequest, studentEntity);
        studentEntity.setUpdateDate(new Date());
        return studentRepository.save(studentEntity);
    }

    public void deleteStudent(Integer id) {
        if (!studentRepository.existsById(id)) {
            throw new AppException(ErrorCode.ID_NOT_FOUND);
        }
        studentRepository.deleteById(id);
    }

    private void validateStudentRequest(StudentRequest studentRequest, boolean isUpdate) {
        if (!isUpdate && studentRepository.existsByCode(studentRequest.getCode())) {
            throw new AppException(ErrorCode.CODE_EXISTED);
        }
        if (isUpdate && studentRequest.getCode() != null &&
                studentRepository.existsByCode(studentRequest.getCode())) {
            throw new AppException(ErrorCode.CODE_EXISTED);
        }
    }

    private void mapRequestToEntity(StudentRequest studentRequest, StudentEntity studentEntity) {
        studentEntity.setName(studentRequest.getName());
        studentEntity.setCode(studentRequest.getCode());
        studentEntity.setClassId(studentRequest.getClassId());
    }

    public ByteArrayInputStream exportStudentsToExcel() {
        List<StudentEntity> students = studentRepository.findAll();
        String filePath = "D:/Excel_Train/students.xlsx";
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fileOut = new FileOutputStream(filePath);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Students");
            int rowNum = 0;

            // Tạo header row
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"ID", "Name", "Code", "Class ID", "Create Date"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Ghi dữ liệu vào các hàng
            for (StudentEntity studentEntity : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(studentEntity.getId());
                row.createCell(1).setCellValue(studentEntity.getName());
                row.createCell(2).setCellValue(studentEntity.getCode());
                row.createCell(3).setCellValue(studentEntity.getClassId());
                row.createCell(4).setCellValue(
                        studentEntity.getCreateDate() != null ? studentEntity.getCreateDate().toString() : "");
            }
            workbook.write(out);
            workbook.write(fileOut);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(ErrorCode.EXPORT_FAILED);
        }
    }

    public void importStudentsFromExcel(ByteArrayInputStream inputStream) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            // Bắt đầu từ dòng thứ 2 để bỏ qua header
            int rowNum = 1;
            while (rowNum <= sheet.getLastRowNum()) {
                Row row = sheet.getRow(rowNum++);
                if (row == null) continue;

                String name = row.getCell(1).getStringCellValue();
                String code = row.getCell(2).getStringCellValue();
                int classId = (int) row.getCell(3).getNumericCellValue();

                if (studentRepository.existsByCode(code)) {
                    continue;
                }

                StudentEntity studentEntity = new StudentEntity();
                studentEntity.setName(name);
                studentEntity.setCode(code);
                studentEntity.setClassId(classId);
                studentEntity.setCreateDate(new Date());

                studentRepository.save(studentEntity);
            }
        } catch (IOException e) {
            throw new AppException(ErrorCode.IMPORT_FAILED);
        }
    }
}
