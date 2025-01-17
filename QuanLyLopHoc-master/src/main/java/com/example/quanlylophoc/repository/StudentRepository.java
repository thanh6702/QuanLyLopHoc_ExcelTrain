package com.example.quanlylophoc.repository;

import com.example.quanlylophoc.entity.ClassEntity;
import com.example.quanlylophoc.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity,Integer> {
    List<StudentEntity> findByNameContainingIgnoreCase(String name);
    List<StudentEntity> findByCodeContainingIgnoreCase(String code);
    @Override
    boolean existsById(Integer id);
    boolean existsByName(String name);
    boolean existsByCode(String code);
}
