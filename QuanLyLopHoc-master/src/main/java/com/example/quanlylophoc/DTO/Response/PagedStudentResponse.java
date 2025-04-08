package com.example.quanlylophoc.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagedStudentResponse {
    private List<StudentInfoResponse> students;
    private int total;
}