package com.example.quanlylophoc.repository;

import com.example.quanlylophoc.entity.HomeRoomTeacherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeRoom_TeacherRepository extends JpaRepository<HomeRoomTeacherEntity,Integer> {
    List<HomeRoomTeacherEntity> findByNameContainingIgnoreCase(String name);
    boolean existsById(Integer id);
    boolean existsByCode(String code);
    boolean existsByClassId(Integer classId);
    List<HomeRoomTeacherEntity> findByCodeContainingIgnoreCase(String code);
}
