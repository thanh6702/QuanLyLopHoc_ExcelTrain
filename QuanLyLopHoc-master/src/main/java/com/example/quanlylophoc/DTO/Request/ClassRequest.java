package com.example.quanlylophoc.DTO.Request;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;


public class ClassRequest {
    private Integer id;
    @Size(min = 3 , message = "NAME_INVALID")
    private String name;
    @Size(min = 3 , message = "CODE_EXISTED")
    private String code;
    private Date createDate;
    private Date updateDate;

    public ClassRequest() {
    }

    public ClassRequest(Integer id, String name, String code, Date createDate, Date updateDate) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
