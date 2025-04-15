package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Request.ManualScheduleRequestDTO;
import com.example.quanlylophoc.DTO.Response.APIResponse;
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
    public ResponseEntity<APIResponse<List<ClassSchedule>>> generateScheduleForClass(@PathVariable Integer classId) {
        try {
            List<ClassSchedule> schedules = scheduleService.generateScheduleForClass(classId);
            return ResponseEntity.ok(APIResponse.success(schedules));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error(400, "RUNTIME_EXCEPTION", ex.getMessage()));
        }
    }
    @GetMapping("/lesson-times")
    public ResponseEntity<APIResponse<List<LessonTimeDTO>>> getLessonTimes() {
        List<LessonTimeDTO> lessonTimes = scheduleService.getLessonTimeTable();
        return ResponseEntity.ok(APIResponse.success(lessonTimes));
    }

    @PostMapping("/manual-create")
    public ResponseEntity<APIResponse<?>> createScheduleManually(@RequestBody ManualScheduleRequestDTO request) {
        try {
            ClassSchedule schedule = scheduleService.createScheduleManually(request);
            return ResponseEntity.ok(APIResponse.success(schedule));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error(400, "RUNTIME_EXCEPTION", ex.getMessage()));
        }
    }



    @GetMapping("/class/{classId}")
    public ResponseEntity<APIResponse<List<ScheduleResponseDTO>>> getScheduleByClass(@PathVariable Integer classId) {
        List<ScheduleResponseDTO> schedule = scheduleService.getFormattedScheduleForClass(classId);
        return ResponseEntity.ok(APIResponse.success(schedule));
    }
}
