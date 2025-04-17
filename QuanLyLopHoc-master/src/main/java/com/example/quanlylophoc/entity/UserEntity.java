package com.example.quanlylophoc.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "avatar_url", length = 1000)
    private String avatarUrl;

    @Column(name = "video_url", length = 1000)
    private String videoUrl;


    @Transient  // Đánh dấu trường này không được lưu vào database
    private List<GrantedAuthority> authorities;


    public UserEntity() {
    }

    // Constructor với authorities
    public UserEntity(String username, String password, List<GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

}
