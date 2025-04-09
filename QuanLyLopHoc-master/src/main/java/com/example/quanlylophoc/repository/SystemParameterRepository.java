package com.example.quanlylophoc.repository;

import com.example.quanlylophoc.entity.SystemParameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemParameterRepository extends JpaRepository<SystemParameter, Long> {
    Optional<SystemParameter> findByKey(String key);
}
