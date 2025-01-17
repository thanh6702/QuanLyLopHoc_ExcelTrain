package com.example.quanlylophoc.service;

import com.example.quanlylophoc.DTO.Request.ClassRequest;
import com.example.quanlylophoc.Exception.AppException;
import com.example.quanlylophoc.Exception.ErrorCode;
import com.example.quanlylophoc.entity.ClassEntity;
import com.example.quanlylophoc.entity.HomeRoomTeacherEntity;
import com.example.quanlylophoc.repository.ClassRepository;
import com.example.quanlylophoc.repository.HomeRoom_TeacherRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class ClassService {
    private final ClassRepository classRepository;
    private final HomeRoom_TeacherRepository homeRoomTeacherRepository;

    public ClassService(ClassRepository classRepository, HomeRoom_TeacherRepository homeRoomTeacherRepository) {
        this.classRepository = classRepository;
        this.homeRoomTeacherRepository = homeRoomTeacherRepository;
    }

    public ClassEntity createClass(ClassRequest classRequest) {
        ClassEntity classEntity = new ClassEntity();
        if(classRepository.existsByName(classRequest.getName())) {
            throw new AppException(ErrorCode.CLASSNAME_EXISTED);
        }
        classEntity.setName(classRequest.getName());
        if(classRepository.existsByCode(classRequest.getCode())) {
            throw new AppException(ErrorCode.CODE_EXISTED);
        }
        classEntity.setCode(classRequest.getCode());
        classEntity.setCreateDate(new Date());
        classEntity.setUpdateDate(null);
        return classRepository.save(classEntity);

    }

    public List<ClassEntity> getListClasses() {
        return classRepository.findAll();
    }
    public List<ClassEntity> searchClassesByName(String name){
        return classRepository.findByNameContainingIgnoreCase(name);
    }
    public List<ClassEntity> searchClassesByCode(String code){
        return classRepository.findByCodeContainingIgnoreCase(code);
    }

    public ClassEntity updateClass(Integer id,ClassRequest classRequest) {
        ClassEntity classEntity = classRepository.findById(id).orElseThrow(()
                -> new AppException(ErrorCode.ID_NOT_FOUND));
        if(classRepository.existsByName(classRequest.getName())) {
            throw new AppException(ErrorCode.CLASSNAME_EXISTED);
        }
        classEntity.setName(classRequest.getName());
        if(classRepository.existsByCode(classRequest.getCode())) {
            throw new AppException(ErrorCode.CODE_EXISTED);
        }
        classEntity.setCode(classRequest.getCode());
        classEntity.setUpdateDate(new Date());
        return classRepository.save(classEntity);
    }

    public List<ClassEntity> getClassesWithNoStudents() {
        return classRepository.findClassesWithNoStudents();
    }
//    public void deleteClassesWithNoStudents() {
//        List<ClassEntity> classesWithNoStudents = getClassesWithNoStudents();
//        for (ClassEntity classEntity : classesWithNoStudents) {
//            classRepository.delete(classEntity);
//        }
//    }
public void deleteClassIfEmpty(int classId) {
    // Lấy danh sách các lớp không có học sinh
    List<ClassEntity> emptyClasses = getClassesWithNoStudents();

    // Kiểm tra nếu lớp cần xóa có trong danh sách
    Optional<ClassEntity> classToDelete = emptyClasses.stream()
            .filter(c -> c.getId() == classId)
            .findFirst();

    if (classToDelete.isPresent()) {
        classRepository.delete(classToDelete.get()); // Xóa lớp
    } else {
        throw new AppException(ErrorCode.CANNOT_DELETE_CLASS);
    }
}

public ByteArrayInputStream exportClassesToExcel() {
    // Giả lập 200 giáo viên
    List<HomeRoomTeacherEntity> teachers = new ArrayList<>();
    for (int i = 1; i <= 200; i++) {
        HomeRoomTeacherEntity teacher = new HomeRoomTeacherEntity();
        teacher.setName("Teacher " + i);
        teachers.add(teacher);
    }
    // Chuyển danh sách giáo viên thành mảng tên giáo viên
    String[] teacherNames = teachers.stream().map(HomeRoomTeacherEntity::getName).toArray(String[]::new);

    List<ClassEntity> classes = classRepository.findAll();
    String filePath = "D:/Excel_Train/class.xlsx";

    try (Workbook workbook = new XSSFWorkbook();
         FileOutputStream fileOut = new FileOutputStream(filePath);
         ByteArrayOutputStream out = new ByteArrayOutputStream()) {

        Sheet sheet = workbook.createSheet("Classes");
        int rowNum = 0;

        // Tạo sheet riêng để lưu danh sách giáo viên (có thể ẩn sheet này trong Excel)
        Sheet teacherSheet = workbook.createSheet("Teachers");
        int teacherRowNum = 0;

        // Tạo vùng dữ liệu danh sách giáo viên
        for (String teacherName : teacherNames) {
            Row row = teacherSheet.createRow(teacherRowNum++);
            row.createCell(0).setCellValue(teacherName);
        }

        // Tạo một Named Range cho danh sách giáo viên
        Name namedRange = workbook.createName();
        namedRange.setNameName("TeacherList");
        namedRange.setRefersToFormula("'Teachers'!$A$1:$A$200");  // Dải ô có danh sách giáo viên

        // Tạo header row cho sheet "Classes"
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"ID", "Name", "Code", "Create Date", "Teacher"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // Tạo DataValidation sử dụng Named Range
        DataValidationConstraint constraint = sheet.getDataValidationHelper()
                .createFormulaListConstraint("TeacherList"); // Tham chiếu đến Named Range
        int maxRow = classes.size();
        CellRangeAddressList addressList = new CellRangeAddressList(1, maxRow, 4, 4);  // Cột "Teacher"
        DataValidation validation = sheet.getDataValidationHelper().createValidation(constraint, addressList);
        sheet.addValidationData(validation);

        // Ghi dữ liệu vào các hàng trong sheet "Classes"
        for (ClassEntity classEntity : classes) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(classEntity.getId());
            row.createCell(1).setCellValue(classEntity.getName());
            row.createCell(2).setCellValue(classEntity.getCode());
            row.createCell(3).setCellValue(classEntity.getCreateDate() != null ? classEntity.getCreateDate().toString() : "");
            row.createCell(4);  // Cột "Teacher" để người dùng chọn
        }

        // Ghi tất cả vào file
        workbook.write(out);
        workbook.write(fileOut);
        return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
        throw new AppException(ErrorCode.EXPORT_FAILED);
    }
}


    public ByteArrayInputStream exportClassesWithTemplate() {
        // Đường dẫn file mẫu
        String templateFilePath = "D:/Excel_Train/template-class.xlsx";

        try (FileInputStream templateInputStream = new FileInputStream(templateFilePath);
             Workbook workbook = new XSSFWorkbook(templateInputStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.getSheetAt(0); // Sheet đầu tiên trong file mẫu
            List<HomeRoomTeacherEntity> teachers = homeRoomTeacherRepository.findAll(); // Query từ database
            String[] teacherNames = teachers.stream()
                    .map(HomeRoomTeacherEntity::getName)
                    .toArray(String[]::new);

            // Tạo dropdown list từ danh sách giáo viên
            DataValidationConstraint constraint = sheet.getDataValidationHelper()
                    .createExplicitListConstraint(teacherNames);

            int startRow = 1; // Bắt đầu từ dòng 2, vì dòng 1 là header
            int endRow = 100; // Số dòng mà bạn muốn dropdown áp dụng
            int dropdownColumn = 4; // Cột thứ 5 (index = 4)

            CellRangeAddressList addressList = new CellRangeAddressList(startRow, endRow, dropdownColumn, dropdownColumn);
            DataValidation validation = sheet.getDataValidationHelper().createValidation(constraint, addressList);
            sheet.addValidationData(validation);

            // Ghi dữ liệu vào Excel (nếu có danh sách lớp học)
            List<ClassEntity> classes = classRepository.findAll();
            int rowNum = startRow;
            for (ClassEntity classEntity : classes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(classEntity.getId());
                row.createCell(1).setCellValue(classEntity.getName());
                row.createCell(2).setCellValue(classEntity.getCode());
                row.createCell(3).setCellValue(classEntity.getCreateDate().toString());
                row.createCell(dropdownColumn); // Để dropdown hoạt động
            }

            // Ghi dữ liệu ra mảng byte
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            throw new AppException(ErrorCode.EXPORT_FAILED);
        }
    }




    // Import Classes from Excel
//    public void importClassesFromExcel(ByteArrayInputStream inputStream) {
//        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
//            Sheet sheet = workbook.getSheetAt(0);
//
//            // Bắt đầu từ dòng thứ 2 để bỏ qua header
//            int rowNum = 1; // Dòng đầu tiên là header
//            while (rowNum <= sheet.getLastRowNum()) {
//                Row row = sheet.getRow(rowNum++);
//                if (row == null) continue;
//
//                String name = row.getCell(1).getStringCellValue();
//                String code = row.getCell(2).getStringCellValue();
//
//                // Skip nếu name hoặc code đã tồn tại
//                if (classRepository.existsByName(name) || classRepository.existsByCode(code)) {
//                    continue;
//                }
//
//                ClassEntity classEntity = new ClassEntity();
//                classEntity.setName(name);
//                classEntity.setCode(code);
//                classEntity.setCreateDate(new Date());
//
//                classRepository.save(classEntity);
//            }
//        } catch (IOException e) {
//            throw new AppException(ErrorCode.IMPORT_FAILED);
//        }
//    }
    public Map<String, Object> importClassesFromExcel(ByteArrayInputStream inputStream) {
        Map<String, Object> response = new HashMap<>();
        List<String> skippedClasses = new ArrayList<>();
        List<ClassEntity> importedClasses = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            // Bắt đầu từ dòng thứ 2 để bỏ qua header
            int rowNum = 1; // Dòng đầu tiên là header
            while (rowNum <= sheet.getLastRowNum()) {
                Row row = sheet.getRow(rowNum++);
                if (row == null) continue;

                String name = row.getCell(1).getStringCellValue();
                String code = row.getCell(2).getStringCellValue();

                // Skip nếu name hoặc code đã tồn tại
                if (classRepository.existsByName(name) || classRepository.existsByCode(code)) {
                    skippedClasses.add("Class with Name: " + name + " or Code: " + code + " already exists.");
                    continue;
                }

                ClassEntity classEntity = new ClassEntity();
                classEntity.setName(name);
                classEntity.setCode(code);
                classEntity.setCreateDate(new Date());

                classRepository.save(classEntity);
                importedClasses.add(classEntity);
            }

            // Trả về kết quả
            response.put("status", "success");
            response.put("importedClasses", importedClasses.size());
            response.put("skippedClasses", skippedClasses);

        } catch (IOException e) {
            response.put("status", "error");
            response.put("message", "File import failed: " + e.getMessage());
        }

        return response;
    }

}


