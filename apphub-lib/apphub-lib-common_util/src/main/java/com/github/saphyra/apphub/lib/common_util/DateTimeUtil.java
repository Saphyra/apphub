package com.github.saphyra.apphub.lib.common_util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Component
public class DateTimeUtil {
    public LocalTime getCurrentTime() {
        return LocalTime.now(ZoneOffset.UTC);
    }

    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    public long toEpochSecond(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime)
            .map(ld -> ld.toEpochSecond(ZoneOffset.UTC))
            .orElse(0L);
    }

    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }
}
