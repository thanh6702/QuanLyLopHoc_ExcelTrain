package com.example.quanlylophoc.DTO.Response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class StudentInfoResponse {
    private int id;
    private String name;
    private String code;
    private Integer classId;
    private Date createDate;
    private Date updateDate;
}
