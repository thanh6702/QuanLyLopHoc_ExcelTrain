package com.example.quanlylophoc.DTO.Request;

import jakarta.validation.constraints.Size;

import java.util.Date;

public class TeacherRequest {
    @Size(min = 3 , message = "NAME_INVALID")
    private String name;
    @Size(min = 3 , message = "CODE_EXISTED")
    private String code;
    private Integer classId;
    private Date createDate;
    private Date updateDate;

    public TeacherRequest(String name, String code, Integer classId, Date createDate, Date updateDate) {
        this.name = name;
        this.code = code;
        this.classId = classId;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
