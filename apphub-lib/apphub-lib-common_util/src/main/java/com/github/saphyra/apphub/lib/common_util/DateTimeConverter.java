package com.github.saphyra.apphub.lib.common_util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.util.Objects.isNull;

//TODO unit test
public class DateTimeConverter {
    public String convertDomain(LocalTime time) {
        if (isNull(time)) {
            return null;
        }
        return time.toString();
    }

    public String convertDomain(LocalDate date) {
        if (isNull(date)) {
            return null;
        }
        return date.toString();
    }

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

    public LocalDate convertToLocalDate(String string) {
        if (isNull(string)) {
            return null;
        }
        return LocalDate.parse(string);
    }

    public LocalTime convertToLocalTime(String string) {
        if (isNull(string)) {
            return null;
        }
        return LocalTime.parse(string);
    }
}
