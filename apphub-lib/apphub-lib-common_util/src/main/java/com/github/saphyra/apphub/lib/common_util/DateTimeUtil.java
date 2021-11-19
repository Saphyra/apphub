package com.github.saphyra.apphub.lib.common_util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Component
public class DateTimeUtil {
    public LocalDateTime getCurrentDate() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    public long toEpochSecond(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime)
            .map(ld -> ld.toEpochSecond(ZoneOffset.UTC))
            .orElse(0L);
    }
}
