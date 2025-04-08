package com.example.quanlylophoc.service;

import com.example.quanlylophoc.DTO.Request.*;
import com.example.quanlylophoc.configuration.SecurityUtil;
import com.example.quanlylophoc.entity.Departments;
import com.example.quanlylophoc.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }
    public Departments create(DepartmentDTO dto) {
        Departments department = Departments.builder()
                .name(dto.getName())
                .createdBy(SecurityUtil.getCurrentUsername())
                .build();
        return departmentRepository.save(department);
    }


    public List<Departments> getAll() {
        return departmentRepository.findAll();
    }


    public Departments getById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }


    public Departments update(Long id, DepartmentDTO dto) {
        Departments existing = getById(id);
        existing.setName(dto.getName());
        return departmentRepository.save(existing);
    }


    public void delete(Long id) {
        Departments department = getById(id);
        departmentRepository.delete(department);
    }

    public List<DepartmentTreeDTO> getFullTree() {
        return departmentRepository.findAll().stream().map(department -> {
            List<DivisionInfoDTO> divisionDTOs = department.getDivisions().stream().map(division -> {
                List<SubjectGroupInfoDTO> subjectGroupDTOs = division.getSubjectGroups().stream().map(group -> {
                    List<SubjectDTO> subjects = group.getSubjects().stream()
                            .map(subject -> SubjectDTO.builder()
                                    .id(subject.getId())
                                    .name(subject.getName())
                                    .build())
                            .toList();

                    return SubjectGroupInfoDTO.builder()
                            .id(group.getId())
                            .name(group.getName())
                            .head(group.getHead() != null ? TeacherInfoDTO.builder().id(group.getHead().getId()).name(group.getHead().getName()).build() : null)
                            .deputy(group.getDeputy() != null ? TeacherInfoDTO.builder().id(group.getDeputy().getId()).name(group.getDeputy().getName()).build() : null)
                            .subjects(subjects)
                            .build();
                }).toList();

                return DivisionInfoDTO.builder()
                        .id(division.getId())
                        .name(division.getName())
                        .subjectGroups(subjectGroupDTOs)
                        .build();
            }).toList();

            return DepartmentTreeDTO.builder()
                    .id(department.getId())
                    .name(department.getName())
                    .divisions(divisionDTOs)
                    .build();
        }).toList();
    }

}
