package com.example.quanlylophoc.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "student")
public class StudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "code")
    private String code;
    @Column(name = "class_id")
    private Integer classId;
    @Column(name = "created_at")
    private Date createDate;
    @Column(name = "updated_at")
    private Date updateDate;
    @ManyToOne
    @JoinColumn(name = "class_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonBackReference
    private ClassEntity classEntity;

}