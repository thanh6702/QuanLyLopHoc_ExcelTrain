package com.example.quanlylophoc.service;

import com.example.quanlylophoc.DTO.Request.TeacherRequest;
import com.example.quanlylophoc.Exception.AppException;
import com.example.quanlylophoc.Exception.ErrorCode;
import com.example.quanlylophoc.entity.HomeRoomTeacherEntity;
import com.example.quanlylophoc.repository.ClassRepository;
import com.example.quanlylophoc.repository.HomeRoom_TeacherRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class HomeRoom_TeacherService {
    private final HomeRoom_TeacherRepository homeRoomTeacherRepository;
    private final ClassRepository classRepository;

    public HomeRoom_TeacherService(HomeRoom_TeacherRepository homeRoomTeacherRepository, ClassRepository classRepository) {
        this.homeRoomTeacherRepository = homeRoomTeacherRepository;
        this.classRepository = classRepository;
    }


    public HomeRoomTeacherEntity createTeacher(TeacherRequest teacherRequest) {
        HomeRoomTeacherEntity homeRoomTeacherEntity = new HomeRoomTeacherEntity();
        homeRoomTeacherEntity.setName(teacherRequest.getName());
        if(homeRoomTeacherRepository.existsByCode(teacherRequest.getCode())) {
            throw new AppException(ErrorCode.CODE_EXISTED);
        }
        homeRoomTeacherEntity.setCode(teacherRequest.getCode());
        if (homeRoomTeacherRepository.existsByClassId(teacherRequest.getClassId())) {
            throw new AppException(ErrorCode.TEACHER_ALREADY_EXISTS_FOR_CLASS); // Đã có giáo viên cho lớp này
        }
        if(!classRepository.existsById(teacherRequest.getClassId())){
            throw new AppException(ErrorCode.CLASS_ID_INVALID);
        }
        homeRoomTeacherEntity.setClassId(teacherRequest.getClassId());
        homeRoomTeacherEntity.setCreateDate(new Date());
        homeRoomTeacherEntity.setUpdateDate(null);
        return homeRoomTeacherRepository.save(homeRoomTeacherEntity);

    }

    public List<HomeRoomTeacherEntity> getAllTeachers() {
        return homeRoomTeacherRepository.findAll();
    }

    // Tìm kiếm và phân trang lớp học theo tên và mã lớp
//    public Page<ClassEntity> searchClasses(String name, String code, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size); // Mỗi trang 10 lớp
//        return classRepository.findByNameContainingAndCodeContaining(name, code, pageable);
//    }
    public List<HomeRoomTeacherEntity> searchTeacherByName(String name){
        return homeRoomTeacherRepository.findByNameContainingIgnoreCase(name);
    }
    public List<HomeRoomTeacherEntity> searchTeacherByCode(String code){
        return homeRoomTeacherRepository.findByCodeContainingIgnoreCase(code);
    }

    public HomeRoomTeacherEntity updateTeacher(Integer id, TeacherRequest teacherRequest) {
        HomeRoomTeacherEntity homeRoomTeacherEntity = homeRoomTeacherRepository.findById(id).orElseThrow(()
                -> new AppException(ErrorCode.ID_NOT_FOUND));
        homeRoomTeacherEntity.setName(teacherRequest.getName());
        homeRoomTeacherEntity.setCode(teacherRequest.getCode());
        homeRoomTeacherEntity.setClassId(teacherRequest.getClassId());
        homeRoomTeacherEntity.setUpdateDate(new Date());
        return homeRoomTeacherRepository.save(homeRoomTeacherEntity);
    }

    public void deleteTeacher(Integer id) {
        if(!homeRoomTeacherRepository.existsById(id)) {
            throw new AppException(ErrorCode.ID_NOT_FOUND);
        }
        homeRoomTeacherRepository.deleteById(id);
    }

    public ByteArrayInputStream exportTeachersToExcel() {
        List<HomeRoomTeacherEntity> teachers = homeRoomTeacherRepository.findAll();
        String filePath = "D:/Excel_Train/teachers.xlsx";  // Đường dẫn tệp cần lưu

        // Kiểm tra nếu thư mục không tồn tại, tạo thư mục mới
        File directory = new File("D:/Excel_Train");
        if (!directory.exists()) {
            directory.mkdirs();  // Tạo thư mục nếu chưa có
        }

        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fileOut = new FileOutputStream(filePath);  // Mở tệp để ghi ra ổ đĩa
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Teachers");
            int rowNum = 0;

            // Tạo header row
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"ID", "Name", "Code", "Class ID", "Create Date"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Ghi dữ liệu vào các hàng
            for (HomeRoomTeacherEntity teacherEntity : teachers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(teacherEntity.getId());
                row.createCell(1).setCellValue(teacherEntity.getName());
                row.createCell(2).setCellValue(teacherEntity.getCode());
                row.createCell(3).setCellValue(teacherEntity.getClassId());
                row.createCell(4).setCellValue(
                        teacherEntity.getCreateDate() != null ? teacherEntity.getCreateDate().toString() : "");
            }

            // Ghi workbook vào ByteArrayOutputStream
            workbook.write(out);

            // Ghi workbook vào file trên ổ đĩa
            workbook.write(fileOut);

            // Trả về ByteArrayInputStream của tệp đã được ghi
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new AppException(ErrorCode.EXPORT_FAILED);
        }
    }

    public List<HomeRoomTeacherEntity> importTeachersFromExcel(ByteArrayInputStream inputStream) {
        List<HomeRoomTeacherEntity> importedTeachers = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowNum = 1;

            while (rowNum <= sheet.getLastRowNum()) {
                Row row = sheet.getRow(rowNum++);
                if (row == null) continue;

                if (row.getCell(1) == null || row.getCell(2) == null || row.getCell(3) == null) {
                    continue; // Bỏ qua nếu bất kỳ ô nào bị null
                }

                String name = row.getCell(1).getStringCellValue();
                String code = row.getCell(2).getStringCellValue();
                int classId = (int) row.getCell(3).getNumericCellValue();

                if (homeRoomTeacherRepository.existsByCode(code)) {
                    continue; // Bỏ qua nếu mã code đã tồn tại
                }

                HomeRoomTeacherEntity teacher = new HomeRoomTeacherEntity();
                teacher.setName(name);
                teacher.setCode(code);
                teacher.setClassId(classId);
                teacher.setCreateDate(new Date());

                homeRoomTeacherRepository.save(teacher);
                importedTeachers.add(teacher);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error processing Excel file", e);
        }

        return importedTeachers; // Trả về danh sách giáo viên được thêm
    }


}
