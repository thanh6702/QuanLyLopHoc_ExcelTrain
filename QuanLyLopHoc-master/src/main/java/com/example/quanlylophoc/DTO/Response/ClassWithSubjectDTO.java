package com.example.quanlylophoc.DTO.Response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ClassWithSubjectDTO {
    private int id;
    private String name;
    private String code;
    private Date createDate;
    private Date updateDate;
    private List<Map<String, Object>> subjects;
}
