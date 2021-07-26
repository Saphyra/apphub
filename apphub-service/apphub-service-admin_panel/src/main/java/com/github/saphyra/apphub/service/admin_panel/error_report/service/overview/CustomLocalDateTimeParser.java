package com.github.saphyra.apphub.service.admin_panel.error_report.service.overview;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
class CustomLocalDateTimeParser {
    public LocalDateTime parse(String time) {
        String[] parts = time == null ? new String[0] : time.trim()
            .split(" ");

        int years = 1970;
        int months = 1;
        int days = 1;

        if (parts.length > 0 && !parts[0].isEmpty()) {
            String[] yearParts = parts[0].split("-");
            years = getOrDefault(yearParts, 0, 1970);
            months = getOrDefault(yearParts, 1, 1);
            days = getOrDefault(yearParts, 2, 1);
        }

        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        int nanos = 0;

        if (parts.length > 1) {
            String[] dayParts = parts[1].split(":");
            hours = getOrDefault(dayParts, 0, 0);
            minutes = getOrDefault(dayParts, 1, 0);
            seconds = convertOrDefault(dayParts, s -> Integer.parseInt(s.split("\\.")[0]), 2, 0);
            nanos = convertOrDefault(parts, s -> getOrDefault(s.split("\\."), 1, 0), 1, 0);
        }

        LocalDate localDate = LocalDate.of(years, months, days);
        LocalTime localTime = LocalTime.of(hours, minutes, seconds, nanos);
        return LocalDateTime.of(localDate, localTime);
    }

    private int getOrDefault(String[] parts, int index, int defaultValue) {
        if (parts.length < index + 1) {
            return defaultValue;
        }
        return Integer.parseInt(parts[index]);
    }

    private int convertOrDefault(String[] parts, Function<String, Integer> mapper, int index, int defaultValue) {
        if (parts.length < index + 1) {
            return defaultValue;
        }
        return mapper.apply(parts[index]);
    }
}
