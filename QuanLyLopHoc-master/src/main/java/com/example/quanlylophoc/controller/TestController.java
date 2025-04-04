package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.entity.ResignationRequest;
import com.example.quanlylophoc.repository.ResignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ResignationRepository resignationRepository;

    @GetMapping("/resignations")
    public List<ResignationRequest> getAllResignations() {
        List<ResignationRequest> resignationRequestList = resignationRepository.findAll();
        return resignationRequestList;
    }
}
