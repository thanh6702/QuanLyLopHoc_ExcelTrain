package com.example.quanlylophoc.repository;

import com.example.quanlylophoc.entity.ClassEntity;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity,Integer> {
//    Page<ClassEntity> findByNameContainingAndCodeContaining(String name, String code, Pageable pageable);
    boolean existsByName(String name);
    boolean existsByCode(String code);
    boolean existsById(Integer id);
    List<ClassEntity> findByNameLike(String name);
    List<ClassEntity> findByNameContainingIgnoreCase(String name);
    List<ClassEntity> findByCodeContainingIgnoreCase(String code);

    @Query("SELECT c FROM ClassEntity c LEFT JOIN c.students s " +
            "GROUP BY c.id " +
            "HAVING COUNT(s) = 0")
    List<ClassEntity> findClassesWithNoStudents();

}
