package com.example.quanlylophoc.service;

import com.example.quanlylophoc.DTO.Request.SubjectGroupDTO;
import com.example.quanlylophoc.configuration.SecurityUtil;
import com.example.quanlylophoc.entity.SubjectGroup;
import com.example.quanlylophoc.repository.SubjectGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectGroupService {
    private final SubjectGroupRepository subjectGroupRepository;

    public SubjectGroupService(SubjectGroupRepository subjectGroupRepository) {
        this.subjectGroupRepository = subjectGroupRepository;
    }

    public SubjectGroup create(SubjectGroupDTO dto) {
        SubjectGroup group = SubjectGroup.builder()
                .name(dto.getName())
                .division(dto.getDivision())
                .head(dto.getHead())
                .deputy(dto.getDeputy())
                .createdBy(SecurityUtil.getCurrentUsername())
                .build();
        return subjectGroupRepository.save(group);
    }

    public List<SubjectGroup> getAll() {
        return subjectGroupRepository.findAll();
    }

    public SubjectGroup getById(Long id) {
        return subjectGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubjectGroup not found with id: " + id));
    }

    public SubjectGroup update(Long id, SubjectGroupDTO dto) {
        SubjectGroup existing = getById(id);
        existing.setName(dto.getName());
        existing.setDivision(dto.getDivision());
        existing.setHead(dto.getHead());
        existing.setDeputy(dto.getDeputy());
        return subjectGroupRepository.save(existing);
    }

    public void delete(Long id) {
        SubjectGroup subjectGroup = getById(id);
        subjectGroupRepository.delete(subjectGroup);
    }
}
