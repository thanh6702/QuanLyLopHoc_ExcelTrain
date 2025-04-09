package com.example.quanlylophoc.DTO.Request;

import lombok.Data;

import java.time.LocalDate;


@Data
public class ManualScheduleRequestDTO {
    private Integer subjectId;
    private Integer teacherId;
    private Integer roomId;
    private Integer classId;
    private LocalDate date;
    private Integer lessonNumber;
}
