package com.example.quanlylophoc.DTO.Request;

import com.example.quanlylophoc.entity.SubjectGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubjectDTO {
    private Long id;
    private String name;
    private SubjectGroup subjectGroup;
}
