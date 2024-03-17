package com.github.saphyra.integration.server.util;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class DateTimeUtil {
    public OffsetDateTime getCurrentOffsetDateTime() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }
}
