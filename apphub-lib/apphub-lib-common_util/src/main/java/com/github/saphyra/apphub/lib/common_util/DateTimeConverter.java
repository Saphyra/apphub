package com.github.saphyra.apphub.lib.common_util;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;

public class DateTimeConverter {
    public String convertDomain(LocalDateTime dateTime) {
        if (isNull(dateTime)) {
            return null;
        }
        return dateTime.toString();
    }

    public LocalDateTime convertToLocalDateTime(String string) {
        if (isNull(string)) {
            return null;
        }
        return LocalDateTime.parse(string);
    }
}
