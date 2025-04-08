package com.example.quanlylophoc.repository;

import com.example.quanlylophoc.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUsername(String username);

    @Query(value = "SELECT * FROM users ORDER BY id LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<UserEntity> findAllUsersWithPagination(@Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "SELECT * FROM users",
            countQuery = "SELECT count(*) FROM users",
            nativeQuery = true)
    Page<UserEntity> findAllUsersWithPageable(Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM users", nativeQuery = true)
    int countTotalUsers();

    @Query(value = """
        SELECT * FROM users
        WHERE (:keyword IS NULL OR LOWER(name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
               OR LOWER(username) LIKE LOWER(CONCAT('%', :keyword, '%')))
        ORDER BY id
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<UserEntity> searchUsersWithPagination(
            @Param("keyword") String keyword,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
        SELECT COUNT(*) FROM users
        WHERE (:keyword IS NULL OR LOWER(name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
               OR LOWER(username) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """, nativeQuery = true)
    int countSearchUsers(@Param("keyword") String keyword);


}
