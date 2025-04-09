package com.example.quanlylophoc.service;
import com.example.quanlylophoc.repository.SystemParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;


@Service
@RequiredArgsConstructor
public class SystemParameterService {

    private final SystemParameterRepository repository;

    private String getValueOrThrow(String key) {
        return repository.findByKey(key)
                .orElseThrow(() -> new RuntimeException("Missing system parameter: " + key))
                .getValue();
    }

    public LocalTime getAsTime(String key) {
        return LocalTime.parse(getValueOrThrow(key));
    }

    public Duration getAsDuration(String key) {
        LocalTime time = LocalTime.parse(getValueOrThrow(key)); // e.g. 00:45:00
        return Duration.ofHours(time.getHour())
                .plusMinutes(time.getMinute())
                .plusSeconds(time.getSecond());
    }

    public int getAsInt(String key) {
        return Integer.parseInt(getValueOrThrow(key));
    }

    public boolean getAsBoolean(String key) {
        return Boolean.parseBoolean(getValueOrThrow(key));
    }
}
