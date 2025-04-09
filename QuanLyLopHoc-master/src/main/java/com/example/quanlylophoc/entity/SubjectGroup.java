package com.example.quanlylophoc.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subject_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "created_by")
    private String createdBy;
    @ManyToOne
    @JoinColumn(name = "division_id")
    @JsonBackReference
    private Division division;

    @ManyToOne
    @JoinColumn(name = "head_id")
    @JsonBackReference(value = "head-subjectGroup")
    private Teacher head;

    @ManyToOne
    @JoinColumn(name = "deputy_id")
    @JsonBackReference(value = "deputy-subjectGroup")
    private Teacher deputy;

    @OneToMany(mappedBy = "subjectGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Subject> subjects = new ArrayList<>();
}
