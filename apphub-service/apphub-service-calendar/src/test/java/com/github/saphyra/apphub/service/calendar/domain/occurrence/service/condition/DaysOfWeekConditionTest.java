package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DaysOfWeekConditionTest {
    private static final LocalDate START_DATE = LocalDate.of(2025, 9, 1);

    private final DaysOfWeekCondition underTest = new DaysOfWeekCondition(List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));

    @Test
    void shouldHaveOccurrence() {
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 9, 1), 2)).isTrue();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 9, 2), 2)).isTrue();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 9, 3), 2)).isFalse();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 9, 4), 2)).isFalse();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 9, 5), 2)).isTrue();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 9, 6), 2)).isTrue();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 9, 7), 2)).isFalse();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 9, 8), 2)).isTrue();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 9, 9), 2)).isTrue();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 9, 10), 2)).isFalse();
    }
}