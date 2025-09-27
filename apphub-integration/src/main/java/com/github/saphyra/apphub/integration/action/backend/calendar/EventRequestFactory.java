package com.github.saphyra.apphub.integration.action.backend.calendar;

import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class EventRequestFactory {
    public static final Integer DEFAULT_REPEAT_FOR_DAYS = 1;
    public static final int DEFAULT_FUTURE_DAYS = 5;
    public static final LocalDate DEFAULT_START_DATE = LocalDate.now().plusMonths(1).withDayOfMonth(1);
    public static final String DEFAULT_TITLE = "default-title";
    public static final String DEFAULT_CONTENT = "default-content";
    public static final LocalTime DEFAULT_TIME = LocalTime.of(12, 0);
    public static final Integer DEFAULT_REPETITION_DATA_EVERY_X_DAYS = 4;
    public static final Integer DEFAULT_EVENT_DURATION = 20;
    public static final LocalDate DEFAULT_END_DATE = DEFAULT_START_DATE.plusDays(DEFAULT_EVENT_DURATION);
    public static final LocalDate NEW_END_DATE = DEFAULT_END_DATE.plusDays(1);
    public static final Integer MAX_EVENT_DURATION_DAYS = 730;
    public static final Integer NEW_REPEAT_FOR_DAYS = 2;
    public static final LocalDate NEW_START_DATE = DEFAULT_START_DATE.plusDays(1);
    public static final String NEW_TITLE = "new-title";
    public static final String NEW_CONTENT = "new-content";
    public static final LocalTime NEW_TIME = LocalTime.of(14, 0);
    public static final Integer NEW_REPETITION_DATA_EVERY_X_DAYS = 5;
    public static final Integer NEW_REMIND_ME_BEFORE_DAYS = 1;
    public static final Object DEFAULT_REPETITION_DATA_DAYS_OF_WEEK = List.of(DayOfWeek.TUESDAY.name(), DayOfWeek.SATURDAY.name());
    public static final Object NEW_REPETITION_DATA_DAYS_OF_WEEK = List.of(DayOfWeek.WEDNESDAY.name(), DayOfWeek.SUNDAY.name());

    public static EventRequest validRequest(RepetitionType repetitionType) {
        return EventRequest.builder()
            .repetitionType(repetitionType)
            .repetitionData(getRepetitionData(repetitionType).getEntity1())
            .repeatForDays(DEFAULT_REPEAT_FOR_DAYS)
            .startDate(DEFAULT_START_DATE)
            .endDate(repetitionType == RepetitionType.ONE_TIME ? null : DEFAULT_END_DATE)
            .time(DEFAULT_TIME)
            .title(DEFAULT_TITLE)
            .content(DEFAULT_CONTENT)
            .remindMeBeforeDays(0)
            .labels(List.of())
            .build();
    }

    public static EventRequest editRequest(RepetitionType repetitionType) {
        return validRequest(repetitionType)
            .toBuilder()
            .repeatForDays(NEW_REPEAT_FOR_DAYS)
            .startDate(NEW_START_DATE)
            .endDate(repetitionType == RepetitionType.ONE_TIME ? null : NEW_END_DATE)
            .time(NEW_TIME)
            .title(NEW_TITLE)
            .content(NEW_CONTENT)
            .remindMeBeforeDays(NEW_REMIND_ME_BEFORE_DAYS)
            .repetitionData(getRepetitionData(repetitionType).getEntity2())
            .build();
    }

    private static BiWrapper<Object, Object> getRepetitionData(RepetitionType repetitionType) {
        return switch (repetitionType) {
            case ONE_TIME -> new BiWrapper<>(null, null);
            case EVERY_X_DAYS -> new BiWrapper<>(DEFAULT_REPETITION_DATA_EVERY_X_DAYS, NEW_REPETITION_DATA_EVERY_X_DAYS);
            case DAYS_OF_WEEK -> new BiWrapper<>(DEFAULT_REPETITION_DATA_DAYS_OF_WEEK, NEW_REPETITION_DATA_DAYS_OF_WEEK);
            case DAYS_OF_MONTH -> new BiWrapper<>(List.of(22, 19), List.of(28, 2));
            default -> throw new IllegalArgumentException("Not implemented for repetitionType: " + repetitionType);
        };
    }
}
