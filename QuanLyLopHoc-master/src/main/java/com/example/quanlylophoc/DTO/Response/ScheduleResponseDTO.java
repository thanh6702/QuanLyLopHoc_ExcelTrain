package com.example.quanlylophoc.DTO.Response;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ScheduleResponseDTO {
    private String className;
    private String subjectName;
    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
