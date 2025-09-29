package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OneTimeConditionTest {
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalDate START_DATE = CURRENT_DATE.minusDays(1);

    @InjectMocks
    private OneTimeCondition underTest;

    @Test
    void getOccurrences() {
        assertThat(underTest.getOccurrences(START_DATE, START_DATE, 3, CURRENT_DATE))
            .containsExactlyInAnyOrder(START_DATE, START_DATE.plusDays(1), START_DATE.plusDays(2));
    }

    @Test
    void getOccurrences_noCurrentDate() {
        assertThat(underTest.getOccurrences(START_DATE, START_DATE, 3, null)).containsExactlyInAnyOrder(START_DATE, CURRENT_DATE, CURRENT_DATE.plusDays(1));
    }
}