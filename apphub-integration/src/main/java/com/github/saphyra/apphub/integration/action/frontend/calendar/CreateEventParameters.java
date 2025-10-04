package com.github.saphyra.apphub.integration.action.frontend.calendar;

import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class CreateEventParameters {
    public static final LocalDate DEFAULT_START_DATE = LocalDate.now()
        .plusMonths(1)
        .withDayOfMonth(7)
        .with(DayOfWeek.MONDAY);
    public static final LocalDate DEFAULT_END_DATE = DEFAULT_START_DATE.plusWeeks(2);
    public static final LocalTime DEFAULT_TIME = LocalTime.of(12, 32);
    public static final String DEFAULT_TITLE = "default-event-title";
    public static final String DEFAULT_DESCRIPTION = "default-event-description";
    public static final Integer DEFAULT_REMIND_ME_BEFORE_DAYS = 0;
    public static final Integer DEFAULT_REPETITION_DATA_EVERY_X_DAYS = 3;
    public static final Object NEW_REPETITION_DATA_EVERY_X_DAYS = 5;
    public static final Integer DEFAULT_REPEAT_FOR_DAYS = 1;
    public static final List<DayOfWeek> DEFAULT_REPETITION_DATA_DAYS_OF_WEEK = List.of(DayOfWeek.TUESDAY, DayOfWeek.SATURDAY);
    public static final List<DayOfWeek> NEW_REPETITION_TYPE_DAYS_OF_WEEK = List.of(DayOfWeek.WEDNESDAY, DayOfWeek.SUNDAY);
    public static final List<Integer> DEFAULT_REPETITION_DATA_DAYS_OF_MONTH = List.of(3, 22);
    public static final List<Integer> NEW_REPETITION_DATA_EVERY_X_DAYS_DAYS_OF_WEEK = List.of(5, 24);
    public static final LocalDate NEW_START_DATE = DEFAULT_START_DATE.plusMonths(1);
    public static final LocalDate NEW_END_DATE = NEW_START_DATE.plusWeeks(2);
    public static final LocalTime NEW_TIME = LocalTime.of(14, 15);
    public static final String NEW_TITLE = "new-event-title";
    public static final String NEW_DESCRIPTION = "new-event-description";


    private Integer repeatForDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime time;
    private String title;
    private String content;
    private RepetitionType repetitionType;
    private Object repetitionData;
    private Integer remindMeBeforeDays;
    @Builder.Default
    private List<String> existingLabels = List.of();
    @Builder.Default
    private List<String> newLabels = List.of();

    public static CreateEventParameters valid(RepetitionType repetitionType) {
        return CreateEventParameters.builder()
            .startDate(DEFAULT_START_DATE)
            .endDate(repetitionType == RepetitionType.ONE_TIME ? null : DEFAULT_END_DATE)
            .time(DEFAULT_TIME)
            .title(DEFAULT_TITLE)
            .content(DEFAULT_DESCRIPTION)
            .repetitionType(repetitionType)
            .repetitionData(getRepetitionData(repetitionType).getEntity1())
            .remindMeBeforeDays(DEFAULT_REMIND_ME_BEFORE_DAYS)
            .repeatForDays(DEFAULT_REPEAT_FOR_DAYS)
            .build();
    }

    public static CreateEventParameters edit(RepetitionType repetitionType) {
        return CreateEventParameters.builder()
            .startDate(NEW_START_DATE)
            .endDate(repetitionType == RepetitionType.ONE_TIME ? null : NEW_END_DATE)
            .time(NEW_TIME)
            .title(NEW_TITLE)
            .content(NEW_DESCRIPTION)
            .repetitionType(repetitionType)
            .repetitionData(getRepetitionData(repetitionType).getEntity1())
            .remindMeBeforeDays(DEFAULT_REMIND_ME_BEFORE_DAYS)
            .repeatForDays(DEFAULT_REPEAT_FOR_DAYS)
            .build();
    }

    private static BiWrapper<Object, Object> getRepetitionData(RepetitionType repetitionType) {
        return switch (repetitionType) {
            case ONE_TIME -> new BiWrapper<>(null, null);
            case EVERY_X_DAYS -> new BiWrapper<>(DEFAULT_REPETITION_DATA_EVERY_X_DAYS, NEW_REPETITION_DATA_EVERY_X_DAYS);
            case DAYS_OF_WEEK -> new BiWrapper<>(DEFAULT_REPETITION_DATA_DAYS_OF_WEEK, NEW_REPETITION_TYPE_DAYS_OF_WEEK);
            case DAYS_OF_MONTH -> new BiWrapper<>(DEFAULT_REPETITION_DATA_DAYS_OF_MONTH, NEW_REPETITION_DATA_EVERY_X_DAYS_DAYS_OF_WEEK);
            default -> throw new IllegalArgumentException("Unsupported repetitionType " + repetitionType);
        };
    }
}
