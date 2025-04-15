package com.example.quanlylophoc.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    @Column(name = "class_id")
    private int classId;

    @ManyToMany(mappedBy = "teachers")
    @JsonManagedReference(value = "teacher-subject")
    private List<Subject> subjects = new ArrayList<>();
//    @OneToMany(mappedBy = "head")
//    @JsonManagedReference(value = "head-subjectGroup")
//    private List<SubjectGroup> headOfGroups = new ArrayList<>();
//
//    @OneToMany(mappedBy = "deputy")
//    @JsonManagedReference(value = "deputy-subjectGroup")
//    private List<SubjectGroup> deputyOfGroups = new ArrayList<>();
}
