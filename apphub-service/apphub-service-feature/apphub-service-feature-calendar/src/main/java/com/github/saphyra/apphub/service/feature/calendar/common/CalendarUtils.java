package com.github.saphyra.apphub.service.feature.calendar.common;

public class CalendarUtils {
    public static <T> T mask(boolean masked, T value, T maskedValue) {
        return masked ? maskedValue : value;
    }
}
