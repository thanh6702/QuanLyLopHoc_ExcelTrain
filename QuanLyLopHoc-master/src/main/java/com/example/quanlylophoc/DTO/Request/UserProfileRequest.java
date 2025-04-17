package com.example.quanlylophoc.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileRequest {
    private String name;
    private String username;
    private String password;
    private MultipartFile avatar;
    private MultipartFile video;
}
