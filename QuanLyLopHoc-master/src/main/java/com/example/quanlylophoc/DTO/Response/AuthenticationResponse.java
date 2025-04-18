package com.example.quanlylophoc.DTO.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data // Hỗ trợ tất cả các getter, setter, constructor...
@Builder // Đây là chú thích cần thiết để sử dụng builder()
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    String token;
    boolean authenticated;

    public AuthenticationResponse() {
    }

    public AuthenticationResponse(String token, boolean authenticated) {
        this.token = token;
        this.authenticated = authenticated;
    }

}
