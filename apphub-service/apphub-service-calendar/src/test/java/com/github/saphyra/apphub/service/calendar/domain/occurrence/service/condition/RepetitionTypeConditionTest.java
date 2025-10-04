package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RepetitionTypeConditionTest {
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalDate START_DATE = CURRENT_DATE.minusDays(1);
    private static final LocalDate END_DATE = CURRENT_DATE.plusDays(2);
    private static final Integer REPEAT_FOR_DAYS = 2;

    @Mock
    private ShouldHaveOccurrenceSupplier shouldHaveOccurrenceSupplier;

    @InjectMocks
    private TestRepetitionTypeCondition underTest;

    @Test
    void getOccurrences() {
        given(shouldHaveOccurrenceSupplier.get(START_DATE, END_DATE, START_DATE, REPEAT_FOR_DAYS)).willReturn(true);
        given(shouldHaveOccurrenceSupplier.get(START_DATE, END_DATE, START_DATE.plusDays(1), REPEAT_FOR_DAYS)).willReturn(true);
        given(shouldHaveOccurrenceSupplier.get(START_DATE, END_DATE, START_DATE.plusDays(2), REPEAT_FOR_DAYS)).willReturn(false);
        given(shouldHaveOccurrenceSupplier.get(START_DATE, END_DATE, START_DATE.plusDays(3), REPEAT_FOR_DAYS)).willReturn(true);

        assertThat(underTest.getOccurrences(START_DATE, END_DATE, REPEAT_FOR_DAYS, CURRENT_DATE)).containsExactlyInAnyOrder(CURRENT_DATE, CURRENT_DATE.plusDays(2));
    }

    @Test
    void getOccurrences_nullCurrentDate() {
        given(shouldHaveOccurrenceSupplier.get(START_DATE, END_DATE, START_DATE, REPEAT_FOR_DAYS)).willReturn(true);
        given(shouldHaveOccurrenceSupplier.get(START_DATE, END_DATE, START_DATE.plusDays(1), REPEAT_FOR_DAYS)).willReturn(true);
        given(shouldHaveOccurrenceSupplier.get(START_DATE, END_DATE, START_DATE.plusDays(2), REPEAT_FOR_DAYS)).willReturn(false);
        given(shouldHaveOccurrenceSupplier.get(START_DATE, END_DATE, START_DATE.plusDays(3), REPEAT_FOR_DAYS)).willReturn(true);

        assertThat(underTest.getOccurrences(START_DATE, END_DATE, REPEAT_FOR_DAYS, null)).containsExactlyInAnyOrder(START_DATE, CURRENT_DATE, CURRENT_DATE.plusDays(2));
    }

    static class TestRepetitionTypeCondition implements RepetitionTypeCondition {
        private final ShouldHaveOccurrenceSupplier shouldHaveOccurrenceSupplier;

        TestRepetitionTypeCondition(ShouldHaveOccurrenceSupplier shouldHaveOccurrenceSupplier) {
            this.shouldHaveOccurrenceSupplier = shouldHaveOccurrenceSupplier;
        }

        @Override
        public boolean shouldHaveOccurrence(LocalDate startDate, LocalDate endDate, LocalDate date, Integer repeatForDays) {
            return shouldHaveOccurrenceSupplier.get(startDate, endDate, date, repeatForDays);
        }
    }

    interface ShouldHaveOccurrenceSupplier {
        boolean get(LocalDate startDate, LocalDate endDate, LocalDate date, Integer repeatForDays);
    }
}