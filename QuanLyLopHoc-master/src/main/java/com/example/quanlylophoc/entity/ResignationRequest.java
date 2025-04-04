package com.example.quanlylophoc.entity;


import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "resignation_requests")
@Getter
@Setter
public class ResignationRequest {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY) // Thêm LAZY để tránh lỗi vòng lặp
//    @JoinColumn(name = "employee_id", nullable = false)
//    private Employee employee;

    @Temporal(TemporalType.DATE) // Đảm bảo kiểu dữ liệu đúng
    private Date resignDate;

    private String reason;
    private String handoverPerson;
    private String handoverDepartment;


}

