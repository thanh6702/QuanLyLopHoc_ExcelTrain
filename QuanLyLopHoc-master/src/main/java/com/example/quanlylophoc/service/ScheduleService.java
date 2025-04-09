package com.example.quanlylophoc.service;

import com.example.quanlylophoc.DTO.Request.ManualScheduleRequestDTO;
import com.example.quanlylophoc.DTO.Response.LessonTimeDTO;
import com.example.quanlylophoc.DTO.Response.ScheduleResponseDTO;
import com.example.quanlylophoc.configuration.SecurityUtil;
import com.example.quanlylophoc.entity.*;
import com.example.quanlylophoc.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final RoomRepository roomRepository;
    private final ClassScheduleRepository classScheduleRepository;
    private final SystemParameterRepository systemParameterRepository;
    private final SystemParameterService systemParameterService;

    public LocalDateTime[] calculateStartAndEndTime(LocalDate date, int lessonNumber) {
        LocalTime morningStart = systemParameterService.getAsTime("morning_start_time");
        LocalTime afternoonStart = systemParameterService.getAsTime("afternoon_start_time");
        Duration lessonDuration = systemParameterService.getAsDuration("lesson_duration_minutes");
        Duration breakDuration = systemParameterService.getAsDuration("break_duration_minutes");

        int offset = lessonNumber - 1;
        LocalTime startTime;

        if (lessonNumber <= 4) {
            Duration totalOffset = lessonDuration.plus(breakDuration).multipliedBy(offset);
            startTime = morningStart.plus(totalOffset);
        } else {
            Duration totalOffset = lessonDuration.plus(breakDuration).multipliedBy(lessonNumber - 5);
            startTime = afternoonStart.plus(totalOffset);
        }

        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = startDateTime.plus(lessonDuration);

        return new LocalDateTime[]{startDateTime, endDateTime};
    }
    public List<LessonTimeDTO> getLessonTimeTable() {
        LocalDate today = LocalDate.now();

        List<LessonTimeDTO> result = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            LocalDateTime[] timeRange = calculateStartAndEndTime(today, i);
            result.add(new LessonTimeDTO(i, timeRange[0], timeRange[1]));
        }
        return result;
    }

    public ClassSchedule createScheduleManually(ManualScheduleRequestDTO request) {
        ClassEntity classEntity = classRepository.findById(Math.toIntExact(request.getClassId()))
                .orElseThrow(() -> new RuntimeException("Class not found"));
        Subject subject = subjectRepository.findById(Long.valueOf(request.getSubjectId()))
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        if (!Integer.valueOf(subject.getClassEntity().getId()).equals(classEntity.getId())) {
            throw new RuntimeException("Môn học không thuộc lớp này.");
        }

        Teacher teacher = teacherRepository.findById(Long.valueOf(request.getTeacherId()))
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        Room room = roomRepository.findById(Long.valueOf(request.getRoomId()))
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Tính thời gian bắt đầu và kết thúc từ ngày và tiết học
        LocalDateTime[] times = calculateStartAndEndTime(request.getDate(), request.getLessonNumber());
        LocalDateTime startTime = times[0];
        LocalDateTime endTime = times[1];

        if (!isRoomAvailable(room.getId(), startTime, endTime)) {
            throw new RuntimeException("Phòng đã có lịch vào thời gian này.");
        }

        ClassSchedule schedule = ClassSchedule.builder()
                .classEntity(classEntity)
                .subject(subject)
                .teacher(teacher)
                .room(room)
                .startTime(startTime)
                .endTime(endTime)
                .createdBy(SecurityUtil.getCurrentUsername())
                .build();

        return classScheduleRepository.save(schedule);
    }


    public List<ScheduleResponseDTO> getFormattedScheduleForClass(Integer classId) {
        List<ClassSchedule> schedules = classScheduleRepository.findByClassEntity_Id(classId);
        List<ScheduleResponseDTO> result = new ArrayList<>();

        for (ClassSchedule schedule : schedules) {
            result.add(ScheduleResponseDTO.builder()
                    .className(schedule.getClassEntity().getName())
                    .subjectName(schedule.getSubject().getName())
                    .roomName(schedule.getRoom().getName())
                    .startTime(schedule.getStartTime())
                    .endTime(schedule.getEndTime())
                    .build());
        }

        return result;
    }

    public List<ClassSchedule> generateScheduleForClass(Integer classId) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        List<Subject> subjects = subjectRepository.findByClassEntity_Id(classId.longValue());
        List<Teacher> teachers = teacherRepository.findAll();
        List<Room> rooms = roomRepository.findAll();

        if (teachers.isEmpty() || rooms.isEmpty()) {
            throw new RuntimeException("Không đủ giáo viên hoặc phòng học");
        }

        LocalDateTime baseTime = LocalDateTime.now()
                .withHour(7)
                .withMinute(30)
                .withSecond(0)
                .withNano(0);

        List<ClassSchedule> savedSchedules = new ArrayList<>();

        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);
            LocalDateTime startTime = baseTime.plusHours(i);
            LocalDateTime endTime = startTime.plusMinutes(45);

            Room availableRoom = rooms.stream()
                    .filter(room -> isRoomAvailable(room.getId(), startTime, endTime))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Không còn phòng trống tại " + startTime));

            Teacher teacher = teachers.get(i % teachers.size());

            ClassSchedule schedule = ClassSchedule.builder()
                    .classEntity(classEntity)
                    .subject(subject)
                    .teacher(teacher)
                    .room(availableRoom)
                    .startTime(startTime)
                    .endTime(endTime)
                    .createdBy(SecurityUtil.getCurrentUsername())
                    .build();

            savedSchedules.add(classScheduleRepository.save(schedule));
        }

        return savedSchedules;
    }

    private boolean isRoomAvailable(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        List<ClassSchedule> booked = classScheduleRepository
                .findByRoom_IdAndStartTimeBetween(roomId, startTime, endTime);
        return booked.isEmpty();
    }
}