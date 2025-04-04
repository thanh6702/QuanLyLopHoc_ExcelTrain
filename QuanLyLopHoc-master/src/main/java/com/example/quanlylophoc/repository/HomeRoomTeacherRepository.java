package com.example.quanlylophoc.repository;

import com.example.quanlylophoc.entity.HomeRoomTeacherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeRoomTeacherRepository extends JpaRepository<HomeRoomTeacherEntity,Integer> {

    @Query("SELECT h FROM HomeRoomTeacherEntity h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<HomeRoomTeacherEntity> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT h FROM HomeRoomTeacherEntity h WHERE LOWER(h.code) LIKE LOWER(CONCAT('%', :code, '%'))")
    List<HomeRoomTeacherEntity> findByCodeContainingIgnoreCase(@Param("code") String code);

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN TRUE ELSE FALSE END FROM HomeRoomTeacherEntity h WHERE h.id = :id")
    boolean existsById(@Param("id") Integer id);

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN TRUE ELSE FALSE END FROM HomeRoomTeacherEntity h WHERE h.code = :code")
    boolean existsByCode(@Param("code") String code);

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN TRUE ELSE FALSE END FROM HomeRoomTeacherEntity h WHERE h.classId = :classId")
    boolean existsByClassId(@Param("classId") Integer classId);

    @Query("SELECT h FROM HomeRoomTeacherEntity h WHERE h.code = :code")
    HomeRoomTeacherEntity findHomeRoomTeacherEntityByCode(@Param("code") String code);
}
