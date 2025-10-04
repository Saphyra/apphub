package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DaysOfWeekConditionTest {
    private static final List<DayOfWeek> DAYS_OF_WEEK = List.of(DayOfWeek.TUESDAY, DayOfWeek.FRIDAY);
    private static final LocalDate START_DATE_MONDAY = LocalDate.now()
        .plusWeeks(1)
        .with(DayOfWeek.MONDAY);
    private static final LocalDate END_DATE = START_DATE_MONDAY.plusWeeks(1)
        .with(DayOfWeek.SUNDAY);

    @ParameterizedTest
    @MethodSource("params")
    void shouldHaveOccurrence(List<DayOfWeek> daysOfWeek, LocalDate startDate, LocalDate endDate, LocalDate date, Integer repeatForDays, boolean expected) {
        DaysOfWeekCondition underTest = new DaysOfWeekCondition(daysOfWeek);

        boolean result = underTest.shouldHaveOccurrence(startDate, endDate, date, repeatForDays);

        assertThat(result).isEqualTo(expected);
    }

    public static Stream<Arguments> params() {
        return Stream.of(
            //Regular occurrence
            Arguments.of(DAYS_OF_WEEK, START_DATE_MONDAY, END_DATE, START_DATE_MONDAY.plusDays(1), 1, true),
            //Not on specified day
            Arguments.of(DAYS_OF_WEEK, START_DATE_MONDAY, END_DATE, START_DATE_MONDAY.plusDays(3), 1, false),
            //Future date
            Arguments.of(DAYS_OF_WEEK, START_DATE_MONDAY, END_DATE, START_DATE_MONDAY.plusDays(1).plusWeeks(2), 1, false),
            //Repeat in range
            Arguments.of(DAYS_OF_WEEK, START_DATE_MONDAY, END_DATE, START_DATE_MONDAY.plusDays(2), 2, true),
            //Repeat after end date
            Arguments.of(DAYS_OF_WEEK, START_DATE_MONDAY, START_DATE_MONDAY.plusDays(1), END_DATE.plusDays(1), 2, false),
            //Repeat start of range
            Arguments.of(DAYS_OF_WEEK, START_DATE_MONDAY.plusDays(2), END_DATE, START_DATE_MONDAY.plusDays(2), 2, false)
        );
    }
}