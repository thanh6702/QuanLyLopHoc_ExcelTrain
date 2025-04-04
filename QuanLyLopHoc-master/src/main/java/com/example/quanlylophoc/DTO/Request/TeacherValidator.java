package com.example.quanlylophoc.DTO.Request;

import com.example.quanlylophoc.DTO.Response.TeacherValidatorResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TeacherValidator {

    public static List<TeacherValidatorResult> validateTeachers(List<TeacherRequest> teachers) {
        List<TeacherValidatorResult> results = new ArrayList<>();

        for (TeacherRequest teacher : teachers) {
            TeacherValidatorResult result = new TeacherValidatorResult();
            result.setTeacher(teacher);  // Gán giáo viên vào kết quả

            // Kiểm tra Name
            if (teacher.getName() == null || teacher.getName().isEmpty()) {
                result.addError("Missing Name");
            }

            // Kiểm tra Code
            if (teacher.getCode() == null || teacher.getCode().isEmpty()) {
                result.addError("Missing Code");
            }

            // Kiểm tra Class ID
            if (teacher.getClassId() == null || teacher.getClassId() <= 0) {
                result.addError("Invalid Class ID");
            }
            if(teacher.getUpdateDate() == null){
                result.addError("Missing Update Date");
            }

            // Kiểm tra Create Date
//            try {
//                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(teacher.getCreateDate());
//            } catch (ParseException e) {
//                result.addError("Invalid Create Date Format");
//            }
            try {
                String createDateStr = String.valueOf(teacher.getCreateDate()); // Chuỗi ngày tháng
                SimpleDateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
                Date date = formatter.parse(createDateStr);
                teacher.setCreateDate(date); // Cập nhật lại vào đối tượng Teacher nếu cần
            } catch (ParseException e) {
                result.addError("Invalid Create Date Format");
            }

            results.add(result);  // Thêm kết quả vào danh sách
        }

        return results;
    }
}

