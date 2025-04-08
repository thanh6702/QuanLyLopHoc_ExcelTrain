package com.example.quanlylophoc.repository;

import com.example.quanlylophoc.entity.ClassEntity;
import com.example.quanlylophoc.entity.UserEntity;
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

    @Query(value = "SELECT * FROM class ORDER BY id LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<ClassEntity> findAllUsersWithPagination(@Param("limit") int limit, @Param("offset") int offset);

    boolean existsByCode(String code);

    @Query(value = """
        SELECT * FROM class
        WHERE (:keyword IS NULL OR LOWER(name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        ORDER BY id
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<ClassEntity> searchClassWithPagination(
            @Param("keyword") String keyword,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
        SELECT COUNT(*) FROM class
        WHERE (:keyword IS NULL OR LOWER(name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """, nativeQuery = true)
    int countSearchClass(@Param("keyword") String keyword);
}