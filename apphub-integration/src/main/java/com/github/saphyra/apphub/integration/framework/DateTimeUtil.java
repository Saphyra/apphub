package com.github.saphyra.apphub.integration.framework;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateTimeUtil {
    public static LocalDate nextSunday(LocalDate reference) {
        return reference.plusWeeks(1)
            .with(DayOfWeek.SUNDAY);
    }

    public static LocalDate nextMonday() {
        return LocalDate.now()
            .plusWeeks(1)
            .with(DayOfWeek.MONDAY);
    }
}
