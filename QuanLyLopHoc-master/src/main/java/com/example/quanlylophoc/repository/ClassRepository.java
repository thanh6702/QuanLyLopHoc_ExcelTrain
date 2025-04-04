package com.example.quanlylophoc.repository;

import com.example.quanlylophoc.entity.ClassEntity;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Integer> {

    @Query("SELECT c FROM ClassEntity c WHERE c.code = :code")
    Optional<ClassEntity> findByCode(@Param("code") String code);

    @Query("SELECT c FROM ClassEntity c WHERE c.name LIKE %:name%")
    List<ClassEntity> findByNameContaining(@Param("name") String name);

    @Query("SELECT c FROM ClassEntity c WHERE c.createDate >= :startDate AND c.createDate <= :endDate")
    List<ClassEntity> findByCreateDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    boolean existsByCode(String code);
}