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
public class DivisionInfoDTO {
    private Long id;
    private String name;
    private List<SubjectGroupInfoDTO> subjectGroups;
}
