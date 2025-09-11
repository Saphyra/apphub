package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DaysOfMonthConditionTest {
    private static final LocalDate START_DATE = LocalDate.of(2025, 9, 1);

    private final DaysOfMonthCondition underTest = new DaysOfMonthCondition(List.of(5, 23));

    @Test
    void shouldHaveOccurrence() {
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 9, 4), 2)).isFalse();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 9, 5), 2)).isTrue();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 9, 6), 2)).isTrue();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 9, 7), 2)).isFalse();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 9, 22), 2)).isFalse();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 9, 23), 2)).isTrue();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 9, 24), 2)).isTrue();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 9, 25), 2)).isFalse();

        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 10, 4), 2)).isFalse();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 10, 5), 2)).isTrue();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 10, 6), 2)).isTrue();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 10, 7), 2)).isFalse();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 10, 22), 2)).isFalse();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 10, 23), 2)).isTrue();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 10, 24), 2)).isTrue();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, LocalDate.of(2025, 10, 25), 2)).isFalse();
    }
}