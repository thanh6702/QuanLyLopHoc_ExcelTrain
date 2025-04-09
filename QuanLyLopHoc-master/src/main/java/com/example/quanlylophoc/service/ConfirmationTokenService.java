package com.example.quanlylophoc.service;

import com.example.quanlylophoc.DTO.Response.ConfirmationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConfirmationTokenService {
    private static final long EXPIRATION_MINUTES = 5;

    private final Map<String, ConfirmationToken> tokenStore = new ConcurrentHashMap<>();


    public String generateForAction(String actionType, Long targetId) {
        String token = UUID.randomUUID().toString();
        String key = buildKey(actionType, targetId);

        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES));
        tokenStore.put(key, confirmationToken);

        return token;
    }


    public boolean verify(String actionType, Long targetId, String inputToken) {
        String key = buildKey(actionType, targetId);
        ConfirmationToken storedToken = tokenStore.get(key);

        if (storedToken == null) return false;
        if (!storedToken.getToken().equals(inputToken)) return false;
        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            tokenStore.remove(key);
            return false;
        }

        tokenStore.remove(key);
        return true;
    }

    private String buildKey(String actionType, Long targetId) {
        return actionType + "_" + targetId;
    }
}
