package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EveryXDaysConditionTest {
    private static final LocalDate START_DATE = LocalDate.of(2025, 9, 1);

    private final EveryXDaysCondition underTest = new EveryXDaysCondition(4);

    @Test
    void shouldHaveOccurrence() {
        assertThat(underTest.shouldHaveOccurrence(START_DATE, START_DATE, 2)).isTrue();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, START_DATE.plusDays(1), 2)).isTrue();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, START_DATE.plusDays(2), 2)).isFalse();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, START_DATE.plusDays(3), 2)).isFalse();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, START_DATE.plusDays(4), 2)).isTrue();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, START_DATE.plusDays(5), 2)).isTrue();
        assertThat(underTest.shouldHaveOccurrence(START_DATE, START_DATE.plusDays(6), 2)).isFalse();
    }
}