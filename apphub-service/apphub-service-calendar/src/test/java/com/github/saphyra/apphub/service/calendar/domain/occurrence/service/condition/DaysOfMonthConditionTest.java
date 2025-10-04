package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DaysOfMonthConditionTest {
    private static final LocalDate START_DATE = LocalDate.now()
        .plusMonths(1)
        .withDayOfMonth(1);
    private static final LocalDate END_DATE = START_DATE.plusMonths(2);
    private static final List<Integer> DAYS_OF_MONTH = List.of(7, 16);

    @ParameterizedTest
    @MethodSource("params")
    void shouldHaveOccurrence(List<Integer> daysOfMonth, LocalDate startDate, LocalDate endDate, LocalDate date, Integer repeatForDays, boolean expected) {
        DaysOfMonthCondition underTest = new DaysOfMonthCondition(daysOfMonth);

        boolean result = underTest.shouldHaveOccurrence(startDate, endDate, date, repeatForDays);

        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> params() {
        return Stream.of(
            //Regular occurrence
            Arguments.of(DAYS_OF_MONTH, START_DATE, END_DATE, START_DATE.plusMonths(1).withDayOfMonth(7), 1, true),
            //Future date
            Arguments.of(DAYS_OF_MONTH, START_DATE, END_DATE, START_DATE.plusMonths(3).withDayOfMonth(7), 1, false),
            //Not on specified day
            Arguments.of(DAYS_OF_MONTH, START_DATE, END_DATE, START_DATE.plusMonths(1).withDayOfMonth(8), 1, false),
            //Repeat in range
            Arguments.of(DAYS_OF_MONTH, START_DATE, END_DATE, START_DATE.plusMonths(1).withDayOfMonth(8), 2, true),
            //Repeat after end date
            Arguments.of(DAYS_OF_MONTH, START_DATE, START_DATE.plusMonths(1).withDayOfMonth(16), END_DATE.plusDays(1), 2, false),
            //Repeat start of range
            Arguments.of(DAYS_OF_MONTH, START_DATE.withDayOfMonth(8), END_DATE, START_DATE, 2, false)
        );
    }
}