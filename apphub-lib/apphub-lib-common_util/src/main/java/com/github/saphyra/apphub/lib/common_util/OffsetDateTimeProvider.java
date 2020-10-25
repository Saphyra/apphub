package com.github.saphyra.apphub.lib.common_util;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
//TODO rename to DateTimeUtil
public class OffsetDateTimeProvider {
    public OffsetDateTime getCurrentDate() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }
}
