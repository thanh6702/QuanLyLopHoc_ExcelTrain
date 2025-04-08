package com.example.quanlylophoc.DTO.Request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubjectGroupInfoDTO {
    private Long id;
    private String name;
    private TeacherInfoDTO head;
    private TeacherInfoDTO deputy;
    private List<SubjectDTO> subjects;
}
