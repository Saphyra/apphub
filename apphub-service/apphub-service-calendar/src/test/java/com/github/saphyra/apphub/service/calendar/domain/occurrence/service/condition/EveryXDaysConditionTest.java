package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EveryXDaysConditionTest {
    private static final Integer DAYS = 4;
    private static final LocalDate START_DATE = LocalDate.now();
    private static final LocalDate END_DATE = START_DATE.plusWeeks(1);

    @ParameterizedTest
    @MethodSource("params")
    void shouldHaveOccurrence(Integer days, LocalDate startDate, LocalDate endDate, LocalDate date, Integer repeatForDays, boolean expected) {
        EveryXDaysCondition underTest = new EveryXDaysCondition(days);

        boolean result = underTest.shouldHaveOccurrence(startDate, endDate, date, repeatForDays);

        assertThat(result).isEqualTo(expected);
    }

    public static Stream<Arguments> params() {
        return Stream.of(
            //Regular occurrence
            Arguments.of(DAYS, START_DATE, END_DATE, START_DATE.plusDays(DAYS), 1, true)
            //Future date
            , Arguments.of(DAYS, START_DATE, START_DATE.plusDays(DAYS - 1), START_DATE.plusDays(DAYS), 1, false)
            //Not on specified day
            , Arguments.of(DAYS, START_DATE, END_DATE, START_DATE.plusDays(DAYS - 1), 1, false)
            //Repeat in range
            , Arguments.of(DAYS, START_DATE, END_DATE, START_DATE.plusDays(DAYS + 1), 2, true)
            //Repeat after end date
            , Arguments.of(DAYS, START_DATE, START_DATE.plusDays(DAYS), START_DATE.plusDays(DAYS + 1), 2, false)
        );
    }
}