package com.github.saphyra.apphub.integration.action.backend.calendar;

import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class EventRequestFactory {
    public static final Integer DEFAULT_REPEAT_FOR_DAYS = 1;
    public static final int DEFAULT_FUTURE_DAYS = 5;
    public static final LocalDate DEFAULT_START_DATE = LocalDate.now().plusDays(DEFAULT_FUTURE_DAYS);
    public static final String DEFAULT_TITLE = "default-title";
    public static final String DEFAULT_CONTENT = "default-content";
    public static final LocalTime DEFAULT_TIME = LocalTime.of(12, 0);

    public static EventRequest validRequest(RepetitionType repetitionType) {
        return EventRequest.builder()
            .repetitionType(repetitionType)
            .repetitionData(getRepetitionData(repetitionType))
            .repeatForDays(DEFAULT_REPEAT_FOR_DAYS)
            .startDate(DEFAULT_START_DATE)
            .endDate(repetitionType == RepetitionType.ONE_TIME ? null : DEFAULT_START_DATE.plusDays(1))
            .time(DEFAULT_TIME)
            .title(DEFAULT_TITLE)
            .content(DEFAULT_CONTENT)
            .remindMeBeforeDays(0)
            .labels(List.of())
            .build();
    }

    private static Object getRepetitionData(RepetitionType repetitionType) {
        return switch (repetitionType) {
            case ONE_TIME -> null;
            default -> throw new IllegalArgumentException("Not implemented for repetitionType: " + repetitionType);
        };
    }
}
