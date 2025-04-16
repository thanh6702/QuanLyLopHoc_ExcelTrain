package com.example.quanlylophoc.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    // Thay đổi kiểu dữ liệu tùy theo trường hợp sử dụng
    @Column(name = "created_at")
    private Date createDate;  // Nếu dùng Date

    @Column(name = "updated_at")
    private Date updateDate;  // Nếu dùng Date

//     Hoặc nếu muốn dùng LocalDate
//     @Column(name = "created_at")
//     private LocalDate createDate;
//
//     @Column(name = "updated_at")
//     private LocalDate updateDate;



    //createDate và updateDate sẽ được gán giá trị hiện tại ngay trước khi insert vào DB
//    @PrePersist
//    protected void onCreate() {
//        createDate = updateDate = new Date();
//    }
// Mỗi lần bạn update entity (qua Repository), updateDate sẽ tự set lại.
//    @PreUpdate
//    protected void onUpdate() {
//        updateDate = new Date();
//    }
    @ManyToOne
    @JoinColumn(name = "class_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonBackReference
    private ClassEntity classEntity;

}