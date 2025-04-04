package com.example.quanlylophoc.DTO.Request;

import com.example.quanlylophoc.DTO.Response.ClassValidatorResult;
import com.example.quanlylophoc.DTO.Response.TeacherValidatorResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClassValidator {

    public static List<ClassValidatorResult> validateClass(List<ClassRequest> classRequests) {
        List<ClassValidatorResult> results = new ArrayList<>();

        for (ClassRequest classes : classRequests) {
            ClassValidatorResult result = new ClassValidatorResult();
            result.setClassRequest(classes);  // Gán giáo viên vào kết quả

            // Kiểm tra Name
            if (classes.getName() == null || classes.getName().isEmpty()) {
                result.addError("Missing Name");
            }

            // Kiểm tra Code
            if (classes.getCode() == null || classes.getCode().isEmpty()) {
                result.addError("Missing Code");
            }


            if(classes.getUpdateDate() == null){
                result.addError("Missing Update Date");
            }

            // Kiểm tra Create Date
//            try {
//                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(teacher.getCreateDate());
//            } catch (ParseException e) {
//                result.addError("Invalid Create Date Format");
//            }
            try {
                String createDateStr = String.valueOf(classes.getCreateDate()); // Chuỗi ngày tháng
                SimpleDateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
                Date date = formatter.parse(createDateStr);
                classes.setCreateDate(date); // Cập nhật lại vào đối tượng Teacher nếu cần
            } catch (ParseException e) {
                result.addError("Invalid Create Date Format");
            }

            results.add(result);  // Thêm kết quả vào danh sách
        }

        return results;
    }
}

