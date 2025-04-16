package com.example.quanlylophoc.configuration;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;

@Component
public class DateConfig {

    // Định dạng ngày giờ mặc định: dd/MM/yyyy HH:mm:ss
    private static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private final SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT);

    public Date now() {
        return new Date();
    }


    public Date fromString(String dateStr) throws ParseException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new ParseException("Date string is null or empty", 0);
        }
        return formatter.parse(dateStr);
    }

    public String toString(Date date) {
        if (date == null) return null;
        return formatter.format(date);
    }

    // Chuyển từ java.util.Date sang java.time.LocalDate (chỉ lấy ngày)
    public LocalDate toLocalDate(Date date) {
        if (date == null) return null;

        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    // Chuyển từ java.util.Date sang java.time.LocalTime (chỉ lấy giờ, phút, giây)
    public LocalTime toLocalTime(Date date) {
        if (date == null) return null;

        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalTime();
    }

    // Chuyển từ java.util.Date sang java.time.LocalDateTime (đầy đủ ngày & giờ)
    public LocalDateTime toLocalDateTime(Date date) {
        if (date == null) return null;

        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

}
