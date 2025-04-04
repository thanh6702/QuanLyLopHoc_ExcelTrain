package com.example.quanlylophoc.repository;

import com.example.quanlylophoc.entity.ResignationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ResignationRepository extends JpaRepository<ResignationRequest, Long> {


}
