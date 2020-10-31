package com.github.saphyra.apphub.lib.common_util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class DateTimeUtil {
    public LocalDateTime getCurrentDate() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}
