package com.example.quanlylophoc.service;

import com.example.quanlylophoc.DTO.Request.ClassRequest;
import com.example.quanlylophoc.DTO.Request.StudentRequest;
import com.example.quanlylophoc.DTO.Response.PagedStudentResponse;
import com.example.quanlylophoc.DTO.Response.PagedUserResponse;
import com.example.quanlylophoc.DTO.Response.StudentInfoResponse;
import com.example.quanlylophoc.DTO.Response.UserInfoResponse;
import com.example.quanlylophoc.Exception.AppException;
import com.example.quanlylophoc.Exception.ErrorCode;
import com.example.quanlylophoc.configuration.DateConfig;
import com.example.quanlylophoc.entity.ClassEntity;
import com.example.quanlylophoc.entity.StudentEntity;
import com.example.quanlylophoc.entity.UserEntity;
import com.example.quanlylophoc.repository.ClassRepository;
import com.example.quanlylophoc.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final DateConfig dateConfig;

    public StudentService(StudentRepository studentRepository, DateConfig dateConfig) {
        this.studentRepository = studentRepository;
        this.dateConfig = dateConfig;
    }


    public StudentEntity createStudent(StudentRequest studentRequest) {
        validateStudentRequest(studentRequest, false);
        StudentEntity studentEntity = new StudentEntity();
        mapRequestToEntity(studentRequest, studentEntity);
        //1.
//        if (studentRequest.getCreateDate() != null) {
//            studentEntity.setCreateDate(studentRequest.getCreateDate());
//        } else {
//            studentEntity.setCreateDate(dateConfig.now());
//        }
//        if (studentRequest.getUpdateDate() != null) {
//            studentEntity.setUpdateDate(studentRequest.getUpdateDate());
//        } else {
//            studentEntity.setUpdateDate(dateConfig.now());
//        }
        //2.
        try {
            // 1. Xử lý ngày tạo
            if (studentRequest.getCreateDate() != null && !studentRequest.getCreateDate().isEmpty()) {
                studentEntity.setCreateDate(dateConfig.fromString(studentRequest.getCreateDate()));
            } else {
                studentEntity.setCreateDate(dateConfig.now());
            }

            // 2. Xử lý ngày cập nhật
            if (studentRequest.getUpdateDate() != null && !studentRequest.getUpdateDate().isEmpty()) {
                studentEntity.setUpdateDate(dateConfig.fromString(studentRequest.getUpdateDate()));
            } else {
                studentEntity.setUpdateDate(dateConfig.now());
            }

        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format. Expecting dd/MM/yyyy HH:mm:ss", e);
        }

        // studentEntity.setCreateDate(dateConfig.fromLocalDate(LocalDate.now()));
        // studentEntity.setUpdateDate(dateConfig.fromLocalDateTime(LocalDateTime.now()));

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
        studentEntity.setUpdateDate(dateConfig.now());
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

    public byte[] exportStudentsToExcel() {
        log.info("Start exportStudentsToExcel");

        List<StudentEntity> students = studentRepository.findAll();
        log.info("Students fetched: {}", students.size());

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Students");
            CreationHelper createHelper = workbook.getCreationHelper();

            // Định dạng ngày
            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

            int rowNum = 0;
            String[] headers = {"ID", "Name", "Code", "Class ID", "Create Date"};

            // Thêm header
            Row headerRow = sheet.createRow(rowNum++);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Thêm dữ liệu sinh viên
            for (StudentEntity student : students) {
                log.info("Processing student: {}", student.getId());
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getId());
                row.createCell(1).setCellValue(student.getName());
                row.createCell(2).setCellValue(student.getCode());
                row.createCell(3).setCellValue(student.getClassId());

                // Lấy giá trị CreateDate
                Object createDateObj = student.getCreateDate();
                if (createDateObj != null) {
                    Cell dateCell = row.createCell(4);

                    if (createDateObj instanceof LocalDate localDate) {
                        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        dateCell.setCellValue(date);
                    } else if (createDateObj instanceof LocalDateTime localDateTime) {
                        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                        dateCell.setCellValue(date);
                    } else if (createDateObj instanceof Date date) {
                        dateCell.setCellValue(date);
                    } else {
                        log.warn("Unsupported date type: {}", createDateObj.getClass());
                        dateCell.setCellValue(createDateObj.toString());
                    }

                    // Đặt định dạng ngày cho ô
                    dateCell.setCellStyle(dateCellStyle);
                }
            }

            workbook.write(out);
            log.info("Workbook written successfully");
            return out.toByteArray();

        } catch (IOException e) {
            log.error("IOException during exportStudentsToExcel", e);
            throw new AppException(ErrorCode.EXPORT_FAILED);
        } catch (Exception e) {
            log.error("Unexpected error during exportStudentsToExcel", e);
            throw new AppException(ErrorCode.INTERNAL_ERROR);
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

                String name = "";
                String code = "";
                int classId = 0;
                Date createDate = null;

                try {
                    // Lấy dữ liệu từ các cột, kiểm tra nếu có giá trị hợp lệ
                    name = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "";
                    code = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : "";
                    classId = row.getCell(3) != null ? (int) row.getCell(3).getNumericCellValue() : 0;

                    // Lấy dữ liệu cho CreateDate
                    if (row.getCell(4) != null && row.getCell(4).getCellType() == CellType.STRING) {
                        String dateStr = row.getCell(4).getStringCellValue();
                        try {
                            // Chuyển đổi từ String sang Date (dùng SimpleDateFormat nếu cần)
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            createDate = sdf.parse(dateStr);
                        } catch (ParseException e) {
                            log.error("Invalid date format at row {}: {}", rowNum, dateStr);
                        }
                    } else if (row.getCell(4) != null && row.getCell(4).getCellType() == CellType.NUMERIC) {
                        createDate = row.getCell(4).getDateCellValue();
                    }

                } catch (Exception e) {
                    log.error("Error reading data from row {}: {}", rowNum, e.getMessage());
                    continue; // Bỏ qua dòng này nếu có lỗi trong việc đọc dữ liệu
                }

                // Kiểm tra dữ liệu đầu vào hợp lệ
                if (name.isEmpty() || code.isEmpty() || classId == 0) {
                    log.warn("Invalid data at row {}. Skipping.", rowNum);
                    continue; // Bỏ qua dòng này nếu dữ liệu không hợp lệ
                }

                // Kiểm tra nếu sinh viên đã tồn tại
//                if (studentRepository.existsByCode(code)) {
//                    log.warn("Student with code {} already exists. Skipping.", code);
//                    continue;
//                }

                // Tạo đối tượng StudentEntity và lưu vào database
                StudentEntity studentEntity = new StudentEntity();
                studentEntity.setName(name);
                studentEntity.setCode(code);
                studentEntity.setClassId(classId);
                studentEntity.setCreateDate(createDate != null ? createDate : new Date()); // Nếu createDate null thì dùng ngày hiện tại

                try {
                    studentRepository.save(studentEntity);
                } catch (Exception e) {
                    log.error("Error saving student with code {}: {}", studentEntity.getCode(), e.getMessage());
                    continue; // Tiếp tục với sinh viên khác nếu có lỗi
                }
            }
        } catch (IOException e) {
            log.error("IOException while reading Excel file: {}", e.getMessage());
            throw new AppException(ErrorCode.IMPORT_FAILED);
        } catch (Exception e) {
            log.error("Unexpected error while importing students: {}", e.getMessage());
            throw new AppException(ErrorCode.IMPORT_FAILED);
        }
    }

}
