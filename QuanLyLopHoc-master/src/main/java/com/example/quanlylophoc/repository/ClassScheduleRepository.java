package com.example.quanlylophoc.repository;

import com.example.quanlylophoc.entity.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
    List<ClassSchedule> findByClassEntity_Id(Integer classId);
    List<ClassSchedule> findByRoom_IdAndStartTimeBetween(Long roomId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(cs) > 0 FROM ClassSchedule cs WHERE cs.room.id = :roomId")
    boolean existsByRoomId(@Param("roomId") Long roomId);
}
