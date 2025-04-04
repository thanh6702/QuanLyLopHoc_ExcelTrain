package com.example.quanlylophoc.service;

import com.example.quanlylophoc.DTO.Request.ClassRequest;
import com.example.quanlylophoc.DTO.Request.TeacherRequest;
import com.example.quanlylophoc.DTO.Response.ClassValidatorResult;
import com.example.quanlylophoc.DTO.Response.TeacherValidatorResult;
import com.example.quanlylophoc.Exception.AppException;
import com.example.quanlylophoc.Exception.ErrorCode;
import com.example.quanlylophoc.entity.ClassEntity;
import com.example.quanlylophoc.entity.HomeRoomTeacherEntity;
import com.example.quanlylophoc.repository.ClassRepository;
import com.example.quanlylophoc.repository.HomeRoomTeacherRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class ClassService {
    private final ClassRepository classRepository;
    private final HomeRoomTeacherRepository homeRoomTeacherRepository;

    public ClassService(ClassRepository classRepository, HomeRoomTeacherRepository homeRoomTeacherRepository) {
        this.classRepository = classRepository;
        this.homeRoomTeacherRepository = homeRoomTeacherRepository;
    }

    public List<ClassEntity> getAllClasses() {
        return classRepository.findAll();
    }

    // Lấy thông tin lớp học theo ID
    public Optional<ClassEntity> getClassById(int id) {
        return classRepository.findById(id);
    }

    // Lấy thông tin lớp học theo code
    public Optional<ClassEntity> getClassByCode(String code) {
        return classRepository.findByCode(code);
    }

    // Tìm kiếm lớp học theo từ khóa trong tên
    public List<ClassEntity> searchClassesByName(String name) {
        return classRepository.findByNameContaining(name);
    }

    // Lấy danh sách lớp học trong khoảng ngày tạo
    public List<ClassEntity> getClassesCreatedBetween(Date startDate, Date endDate) {
        return classRepository.findByCreateDateBetween(startDate, endDate);
    }

    // Tạo mới hoặc cập nhật thông tin lớp học
    public ClassEntity saveClass(ClassEntity classEntity) {
        classEntity.setUpdateDate(new Date()); // Cập nhật ngày sửa đổi
        return classRepository.save(classEntity);
    }

    // Xóa lớp học theo ID
    public void deleteClassById(int id) {
        classRepository.deleteById(id);
    }


    public byte[] exportClassToExcel() throws IOException {
        // Đọc file mẫu từ resources
        ClassPathResource resource = new ClassPathResource("template-class.xlsx");
        Workbook workbook = new XSSFWorkbook(resource.getInputStream());

        // Lấy sheet đầu tiên trong file mẫu
        Sheet sheet = workbook.getSheetAt(0);

        // Ghi dữ liệu vào file
        List<ClassEntity> classes = classRepository.findAll();
        int startRow = 3; // Bắt đầu ghi từ hàng thứ 2 (giả sử hàng 1 là header)

        // Tạo CellStyle để format ngày tháng
        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

// Tạo dòng và điền dữ liệu
        for (ClassEntity classEntity : classes) {
            Row row = sheet.createRow(startRow++);
            row.createCell(1).setCellValue(classEntity.getName()); // Cột tên giáo viên
            row.createCell(2).setCellValue(classEntity.getCode()); // Cột code


            // Cột createDate
            Cell createDateCell = row.createCell(3);
            if (classEntity.getCreateDate() != null) {
                createDateCell.setCellValue(classEntity.getCreateDate());
                createDateCell.setCellStyle(dateCellStyle);
            }

            // Cột updateDate
            Cell updateDateCell = row.createCell(4);
            if (classEntity.getUpdateDate() != null) {
                updateDateCell.setCellValue(classEntity.getUpdateDate());
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


    public byte[] generateErrorExcel(List<ClassValidatorResult> validationResults) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Error Records");

        // Định nghĩa style cho lỗi
        CellStyle errorCellStyle = workbook.createCellStyle();
        errorCellStyle.setBorderTop(BorderStyle.THICK);
        errorCellStyle.setBorderBottom(BorderStyle.THICK);
        errorCellStyle.setBorderLeft(BorderStyle.THICK);
        errorCellStyle.setBorderRight(BorderStyle.THICK);


        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Name", "Code","Create Date", "Update Date", "Errors"};

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
        for (ClassValidatorResult errorRecords : validationResults) {
            ClassRequest classRequest = errorRecords.getClassRequest();
            List<String> errors = errorRecords.getErrors();

            Row row = sheet.createRow(rowIndex++);
            Cell idCell = row.createCell(0);
            idCell.setCellValue(classRequest.getId() != null ? classRequest.getId() : 0);

            Cell nameCell = row.createCell(1);
            nameCell.setCellValue(classRequest.getName() != null ? classRequest.getName() : "");

            Cell codeCell = row.createCell(2);
            codeCell.setCellValue(classRequest.getCode() != null ? classRequest.getCode() : "");


            Cell createDateCell = row.createCell(3);
            createDateCell.setCellValue(classRequest.getCreateDate() != null ? classRequest.getCreateDate().toString() : "");

            Cell updateDateCell = row.createCell(4);
            updateDateCell.setCellValue(classRequest.getUpdateDate() != null ? classRequest.getUpdateDate().toString() : "");

            CellStyle errorColumnStyle = workbook.createCellStyle();
            Font redFont = workbook.createFont();
            redFont.setColor(IndexedColors.RED.getIndex());
            errorColumnStyle.setFont(redFont);

            Cell errorCell = row.createCell(5);
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

    public List<ClassRequest> parseTeachersFromExcel(MultipartFile file) throws IOException {
        List<ClassRequest> classRequests = new ArrayList<>();

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

            ClassRequest classRe = new ClassRequest();
            classRe.setId(i);
            classRe.setCode(getStringCellValue(row.getCell(1)));
            classRe.setName(getStringCellValue(row.getCell(2)));
            classRe.setCreateDate(getDateCellValue(row.getCell(3)));
            classRe.setUpdateDate(getDateCellValue(row.getCell(4)));

            classRequests.add(classRe);

        }

        workbook.close();
        return classRequests;
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


    public Map<String, Object> importClassesFromExcel(ByteArrayInputStream inputStream) {
        Map<String, Object> response = new HashMap<>();
        List<String> skippedClasses = new ArrayList<>();
        List<ClassEntity> importedClasses = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            int rowNum = 1;
            while (rowNum <= sheet.getLastRowNum()) {
                Row row = sheet.getRow(rowNum++);
                if (row == null) continue;

                String name = row.getCell(1).getStringCellValue();
                String code = row.getCell(2).getStringCellValue();

                // Kiểm tra xem class với mã code đã tồn tại chưa
                if (classRepository.existsByCode(code)) {
                    skippedClasses.add("Class with Code: " + code + " already exists.");
                    continue;
                }

                ClassEntity classEntity = new ClassEntity();
                classEntity.setName(name);
                classEntity.setCode(code);
                classEntity.setCreateDate(new Date());

                classRepository.save(classEntity);
                importedClasses.add(classEntity);
            }

            response.put("status", "success");
            response.put("importedClasses", importedClasses.size());
            response.put("skippedClasses", skippedClasses);
        } catch (IOException e) {
            response.put("status", "error");
            response.put("message", "File import failed: " + e.getMessage());
        }

        return response;
    }


    public ByteArrayInputStream exportErrorFile(List<String> skippedClasses) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Errors");

            // Tạo header cho file lỗi
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Class Name");
            headerRow.createCell(1).setCellValue("Class Code");
            headerRow.createCell(2).setCellValue("Error Message");

            // Tạo style cho các ô có lỗi (cell có viền đậm)
            CellStyle errorStyle = workbook.createCellStyle();
            BorderStyle borderStyle = BorderStyle.THICK;
            errorStyle.setBorderBottom(borderStyle);
            errorStyle.setBorderTop(borderStyle);
            errorStyle.setBorderLeft(borderStyle);
            errorStyle.setBorderRight(borderStyle);

            // Thêm dữ liệu lỗi vào file Excel
            int rowNum = 1;
            for (String errorMessage : skippedClasses) {
                Row row = sheet.createRow(rowNum++);
                String[] errorDetails = errorMessage.split(","); // Giả sử thông báo lỗi có định dạng "Class Name, Class Code, Error Message"

                if (errorDetails.length >= 3) {
                    // Lọc các dữ liệu và hiển thị vào các cột
                    Cell cell1 = row.createCell(0);
                    cell1.setCellValue(errorDetails[0]);
                    cell1.setCellStyle(errorStyle);  // Áp dụng style cho ô có lỗi

                    Cell cell2 = row.createCell(1);
                    cell2.setCellValue(errorDetails[1]);
                    cell2.setCellStyle(errorStyle);  // Áp dụng style cho ô có lỗi

                    Cell cell3 = row.createCell(2);
                    cell3.setCellValue(errorDetails[2]);
                    cell3.setCellStyle(errorStyle);  // Áp dụng style cho ô có lỗi
                }
            }

            // Ghi dữ liệu vào ByteArrayOutputStream để trả về dưới dạng ByteArrayInputStream
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return new ByteArrayInputStream(outputStream.toByteArray());
            }
        } catch (IOException e) {
            throw new AppException(ErrorCode.EXPORT_FAILED);
        }
    }
}


