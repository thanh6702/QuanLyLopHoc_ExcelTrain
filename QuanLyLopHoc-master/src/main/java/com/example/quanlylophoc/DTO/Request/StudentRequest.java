package com.example.quanlylophoc.DTO.Request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
public class StudentRequest {
    @Size(min = 3 , message = "NAME_INVALID")
    private String name;
    @Size(min = 3 , message = "CODE_EXISTED")
    private String code;
    private Integer classId;

//    private Date createDate;
//    private Date updateDate;

    private String createDate;
    private String updateDate;
}
