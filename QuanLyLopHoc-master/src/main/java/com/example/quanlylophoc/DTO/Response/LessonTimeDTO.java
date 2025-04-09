package com.example.quanlylophoc.DTO.Response;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LessonTimeDTO {
    private int lessonNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
