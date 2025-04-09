package com.example.quanlylophoc.DTO.Request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleRequest {
    private Integer classId;
    private Long subjectId;
    private Long roomId;
    private Long teacherId;
    private LocalDateTime startTime;
}
