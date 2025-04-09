package com.example.quanlylophoc.repository;

import com.example.quanlylophoc.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findByClassEntity_Id(Long classId);

}
