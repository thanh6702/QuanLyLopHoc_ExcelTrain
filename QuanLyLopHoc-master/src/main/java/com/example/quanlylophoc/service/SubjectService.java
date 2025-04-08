package com.example.quanlylophoc.service;

import com.example.quanlylophoc.DTO.Request.SubjectDTO;
import com.example.quanlylophoc.configuration.SecurityUtil;
import com.example.quanlylophoc.entity.Subject;
import com.example.quanlylophoc.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {
    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public Subject create(SubjectDTO dto) {
        Subject subject = Subject.builder()
                .name(dto.getName())
                .subjectGroup(dto.getSubjectGroup())
                .createdBy(SecurityUtil.getCurrentUsername())
                .build();
        return subjectRepository.save(subject);
    }

    public List<Subject> getAll() {
        return subjectRepository.findAll();
    }

    public Subject getById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));
    }

    public Subject update(Long id, SubjectDTO dto) {
        Subject existing = getById(id);
        existing.setName(dto.getName());
        existing.setSubjectGroup(dto.getSubjectGroup());
        return subjectRepository.save(existing);
    }

    public void delete(Long id) {
        Subject subject = getById(id);
        subjectRepository.delete(subject);
    }
}
