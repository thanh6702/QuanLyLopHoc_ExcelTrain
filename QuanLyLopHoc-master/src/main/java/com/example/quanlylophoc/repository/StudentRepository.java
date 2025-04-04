package com.example.quanlylophoc.repository;

import com.example.quanlylophoc.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity,Integer> {
    @Query("SELECT s FROM StudentEntity s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<StudentEntity> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT s FROM StudentEntity s WHERE LOWER(s.code) LIKE LOWER(CONCAT('%', :code, '%'))")
    List<StudentEntity> findByCodeContainingIgnoreCase(@Param("code") String code);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM StudentEntity s WHERE s.id = :id")
    boolean existsById(@Param("id") Integer id);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM StudentEntity s WHERE LOWER(s.name) = LOWER(:name)")
    boolean existsByName(@Param("name") String name);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM StudentEntity s WHERE LOWER(s.code) = LOWER(:code)")
    boolean existsByCode(@Param("code") String code);
}
