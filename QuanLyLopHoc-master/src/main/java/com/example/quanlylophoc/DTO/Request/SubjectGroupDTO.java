package com.example.quanlylophoc.DTO.Request;

import com.example.quanlylophoc.entity.Division;
import com.example.quanlylophoc.entity.Teacher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubjectGroupDTO {
    private Long id;
    private String name;
    private Division division;
    private Teacher head;
    private Teacher deputy;
}
