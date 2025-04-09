package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Request.ManualScheduleRequestDTO;
import com.example.quanlylophoc.DTO.Response.LessonTimeDTO;
import com.example.quanlylophoc.DTO.Response.ScheduleResponseDTO;
import com.example.quanlylophoc.entity.ClassSchedule;
import com.example.quanlylophoc.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/generate/{classId}")
    public ResponseEntity<?> generateScheduleForClass(@PathVariable Integer classId) {
        try {
            List<ClassSchedule> schedules = scheduleService.generateScheduleForClass(classId);
            return ResponseEntity.ok(schedules);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    @GetMapping("/lesson-times")
    public List<LessonTimeDTO> getLessonTimes() {
        return scheduleService.getLessonTimeTable();
    }

    @PostMapping("/manual-create")
    public ResponseEntity<?> createScheduleManually(@RequestBody ManualScheduleRequestDTO request) {
        try {
            ClassSchedule schedule = scheduleService.createScheduleManually(request);
            return ResponseEntity.ok(schedule);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }


    @GetMapping("/class/{classId}")
    public ResponseEntity<List<ScheduleResponseDTO>> getScheduleByClass(@PathVariable Integer classId) {
        List<ScheduleResponseDTO> schedule = scheduleService.getFormattedScheduleForClass(classId);
        return ResponseEntity.ok(schedule);
    }
}
