package com.example.quanlylophoc.service;

import com.example.quanlylophoc.DTO.Request.TeacherRequest;
import com.example.quanlylophoc.DTO.Response.TeacherValidatorResult;
import com.example.quanlylophoc.Exception.AppException;
import com.example.quanlylophoc.Exception.ErrorCode;
import com.example.quanlylophoc.entity.HomeRoomTeacherEntity;
import com.example.quanlylophoc.repository.ClassRepository;
import com.example.quanlylophoc.repository.HomeRoomTeacherRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class HomeRoomTeacherService {
    private final HomeRoomTeacherRepository homeRoomTeacherRepository;
    private final ClassRepository classRepository;

    public HomeRoomTeacherService(HomeRoomTeacherRepository homeRoomTeacherRepository, ClassRepository classRepository) {
        this.homeRoomTeacherRepository = homeRoomTeacherRepository;
        this.classRepository = classRepository;
    }
    private List<HomeRoomTeacherEntity> errorTeachers = new ArrayList<>();
    private List<String> errorMessages = new ArrayList<>();

    public HomeRoomTeacherEntity createTeacher(TeacherRequest teacherRequest) {
        if (homeRoomTeacherRepository.existsByCode(teacherRequest.getCode())) {
            throw new AppException(ErrorCode.CODE_EXISTED);
        }

        if (homeRoomTeacherRepository.existsByClassId(teacherRequest.getClassId())) {
            throw new AppException(ErrorCode.TEACHER_ALREADY_EXISTS_FOR_CLASS);
        }

        if (!classRepository.existsById(teacherRequest.getClassId())) {
            throw new AppException(ErrorCode.CLASS_ID_INVALID);
        }

        HomeRoomTeacherEntity homeRoomTeacherEntity = new HomeRoomTeacherEntity();
        homeRoomTeacherEntity.setName(teacherRequest.getName());
        homeRoomTeacherEntity.setCode(teacherRequest.getCode());
        homeRoomTeacherEntity.setClassId(teacherRequest.getClassId());
        homeRoomTeacherEntity.setCreateDate(new Date());
        homeRoomTeacherEntity.setUpdateDate(null);

        return homeRoomTeacherRepository.save(homeRoomTeacherEntity);
    }

    public List<HomeRoomTeacherEntity> getAllTeachers() {
        return homeRoomTeacherRepository.findAll();
    }

    public List<HomeRoomTeacherEntity> searchTeacherByName(String name) {
        return homeRoomTeacherRepository.findByNameContainingIgnoreCase(name);
    }

    public List<HomeRoomTeacherEntity> searchTeacherByCode(String code) {
        return homeRoomTeacherRepository.findByCodeContainingIgnoreCase(code);
    }

    public HomeRoomTeacherEntity updateTeacher(Integer id, TeacherRequest teacherRequest) {
        HomeRoomTeacherEntity homeRoomTeacherEntity = homeRoomTeacherRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ID_NOT_FOUND));

        if (!homeRoomTeacherEntity.getCode().equals(teacherRequest.getCode()) && homeRoomTeacherRepository.existsByCode(teacherRequest.getCode())) {
            throw new AppException(ErrorCode.CODE_EXISTED);
        }

        if (!homeRoomTeacherEntity.getClassId().equals(teacherRequest.getClassId()) && homeRoomTeacherRepository.existsByClassId(teacherRequest.getClassId())) {
            throw new AppException(ErrorCode.TEACHER_ALREADY_EXISTS_FOR_CLASS);
        }

        if (!classRepository.existsById(teacherRequest.getClassId())) {
            throw new AppException(ErrorCode.CLASS_ID_INVALID);
        }

        homeRoomTeacherEntity.setName(teacherRequest.getName());
        homeRoomTeacherEntity.setCode(teacherRequest.getCode());
        homeRoomTeacherEntity.setClassId(teacherRequest.getClassId());
        homeRoomTeacherEntity.setUpdateDate(new Date());

        return homeRoomTeacherRepository.save(homeRoomTeacherEntity);
    }

    public void deleteTeacher(Integer id) {
        if (!homeRoomTeacherRepository.existsById(id)) {
            throw new AppException(ErrorCode.ID_NOT_FOUND);
        }
        homeRoomTeacherRepository.deleteById(id);
    }


public byte[] exportTeachersToExcel() throws IOException {
    // Đọc file mẫu từ resources
    ClassPathResource resource = new ClassPathResource("template-teacher.xlsx");
    Workbook workbook = new XSSFWorkbook(resource.getInputStream());

    // Lấy sheet đầu tiên trong file mẫu
    Sheet sheet = workbook.getSheetAt(0);

    // Ghi dữ liệu vào file
    List<HomeRoomTeacherEntity> teachers = homeRoomTeacherRepository.findAll();
    int startRow = 3; // Bắt đầu ghi từ hàng thứ 2 (giả sử hàng 1 là header)

    // Tạo CellStyle để format ngày tháng
    CellStyle dateCellStyle = workbook.createCellStyle();
    CreationHelper createHelper = workbook.getCreationHelper();
    dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

// Tạo dòng và điền dữ liệu
    for (HomeRoomTeacherEntity teacher : teachers) {
        Row row = sheet.createRow(startRow++);
        row.createCell(1).setCellValue(teacher.getName()); // Cột tên giáo viên
        row.createCell(2).setCellValue(teacher.getCode()); // Cột code
        row.createCell(3).setCellValue(teacher.getClassId()); // Cột mã lớp

        // Cột createDate
        Cell createDateCell = row.createCell(4);
        if (teacher.getCreateDate() != null) {
            createDateCell.setCellValue(teacher.getCreateDate());
            createDateCell.setCellStyle(dateCellStyle);
        }

        // Cột updateDate
        Cell updateDateCell = row.createCell(5);
        if (teacher.getUpdateDate() != null) {
            updateDateCell.setCellValue(teacher.getUpdateDate());
            updateDateCell.setCellStyle(dateCellStyle);
        }
    }

    // Lưu workbook vào ByteArrayOutputStream
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    workbook.write(outputStream);
    workbook.close();

    // Trả về dữ liệu file dưới dạng byte[]
    return outputStream.toByteArray();
}

    public byte[] generateErrorExcel(List<TeacherValidatorResult> validationResults) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Error Records");

        // Định nghĩa style cho lỗi
        CellStyle errorCellStyle = workbook.createCellStyle();
        errorCellStyle.setBorderTop(BorderStyle.THICK);
        errorCellStyle.setBorderBottom(BorderStyle.THICK);
        errorCellStyle.setBorderLeft(BorderStyle.THICK);
        errorCellStyle.setBorderRight(BorderStyle.THICK);


        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Name", "Code", "Class ID", "Create Date", "Update Date", "Errors"};

        // Style cho header
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }


        int rowIndex = 1;
        for (TeacherValidatorResult errorRecords : validationResults) {
            TeacherRequest teacher = errorRecords.getTeacher();
            List<String> errors = errorRecords.getErrors();

            Row row = sheet.createRow(rowIndex++);
            Cell idCell = row.createCell(0);
            idCell.setCellValue(teacher.getId() != null ? teacher.getId() : 0);

            Cell nameCell = row.createCell(1);
            nameCell.setCellValue(teacher.getName() != null ? teacher.getName() : "");

            Cell codeCell = row.createCell(2);
            codeCell.setCellValue(teacher.getCode() != null ? teacher.getCode() : "");

            Cell classIdCell = row.createCell(3);
            classIdCell.setCellValue(teacher.getClassId() != null ? teacher.getClassId() : 0);

            Cell createDateCell = row.createCell(4);
            createDateCell.setCellValue(teacher.getCreateDate() != null ? teacher.getCreateDate().toString() : "");

            Cell updateDateCell = row.createCell(5);
            updateDateCell.setCellValue(teacher.getUpdateDate() != null ? teacher.getUpdateDate().toString() : "");

            CellStyle errorColumnStyle = workbook.createCellStyle();
            Font redFont = workbook.createFont();
            redFont.setColor(IndexedColors.RED.getIndex());
            errorColumnStyle.setFont(redFont);

            Cell errorCell = row.createCell(6);
            if (!errors.isEmpty()){
                errorCell.setCellValue(String.join("\n", errors));
            }else {
                errorCell.setCellValue("");
            }
            errorCell.setCellStyle(errorColumnStyle);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }




    public List<String> importTeachersFromExcel(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.EXPORT_FAILED);
        }
        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            throw new AppException(ErrorCode.EXPORT_FAILED);
        }
        List<String> errorMessages = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            int currentRowIndex = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                currentRowIndex++;
                if (currentRowIndex < 4) {
                    continue;
                }
                if (isRowEmpty(row)) {
                    break;
                }
                Cell codeCell = row.getCell(1);
                if (codeCell == null || codeCell.getStringCellValue().trim().isEmpty()) {
                    errorMessages.add("Dòng " + (row.getRowNum() + 1) + ": Mã giáo viên không được để trống.");
                    continue;
                }
                String teacherCode = codeCell.getStringCellValue().trim();
                HomeRoomTeacherEntity existingTeacher = homeRoomTeacherRepository.findHomeRoomTeacherEntityByCode(teacherCode);
                if (existingTeacher != null) {
                    errorMessages.add("Dòng " + (row.getRowNum() + 1) + ": Mã giáo viên '" + teacherCode + "' đã tồn tại.");
                    continue;
                }
                Cell nameCell = row.getCell(2);
                if (nameCell == null || nameCell.getStringCellValue().trim().isEmpty()) {
                    errorMessages.add("Dòng " + (row.getRowNum() + 1) + ": Tên giáo viên không được để trống.");
                    continue;
                }
                String name = nameCell.getStringCellValue().trim();
                HomeRoomTeacherEntity newTeacher = new HomeRoomTeacherEntity();
                newTeacher.setCode(teacherCode);
                newTeacher.setName(name);
                homeRoomTeacherRepository.save(newTeacher);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel file.", e);
        }
        return errorMessages;
    }

    private boolean isRowEmpty(Row row) {
        if(row == null) {
            return true;
        }
        return false;
    }

    public List<TeacherRequest> parseTeachersFromExcel(MultipartFile file) throws IOException {
        List<TeacherRequest> teacherRequests = new ArrayList<>();

        // Kiểm tra định dạng file (phải là Excel)
        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Invalid file format. Only .xlsx files are supported.");
        }
        // Mở file Excel
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên

        // id = 3 means the data start at line 3
        for (int i = 3; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);

            if (row == null) continue;

            TeacherRequest teacher = new TeacherRequest();
            teacher.setId(i);
            teacher.setCode(getStringCellValue(row.getCell(1)));
            teacher.setName(getStringCellValue(row.getCell(2)));
            teacher.setClassId((int) getNumericCellValue(row.getCell(3)));
            teacher.setCreateDate(getDateCellValue(row.getCell(4)));
            teacher.setUpdateDate(getDateCellValue(row.getCell(5)));

            teacherRequests.add(teacher);

        }

        workbook.close();
        return teacherRequests;
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : String.valueOf((int) cell.getNumericCellValue());
    }

    private double getNumericCellValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return 0;
        }
        return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : 0;
    }

    private Date getDateCellValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        return cell.getCellType() == CellType.NUMERIC ? cell.getDateCellValue() : null;
    }


}
