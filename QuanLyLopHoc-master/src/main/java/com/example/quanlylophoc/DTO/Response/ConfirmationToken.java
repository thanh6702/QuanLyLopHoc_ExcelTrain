package com.example.quanlylophoc.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ConfirmationToken {
    private final String token;
    private final LocalDateTime expiresAt;
}
