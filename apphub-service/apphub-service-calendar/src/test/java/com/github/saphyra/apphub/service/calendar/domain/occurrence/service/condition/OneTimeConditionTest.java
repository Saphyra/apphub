package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OneTimeConditionTest {
    private static final LocalDate CURRENT_TIME = LocalDate.now();
    private static final LocalDate START_DATE = CURRENT_TIME.minusDays(1);

    @InjectMocks
    private OneTimeCondition underTest;

    @Test
    void getOccurrences() {
        assertThat(underTest.getOccurrences(START_DATE, START_DATE, 3, CURRENT_TIME)).containsExactlyInAnyOrder(CURRENT_TIME, CURRENT_TIME.plusDays(1));
    }

    @Test
    void getOccurrences_noCurrentDate() {
        assertThat(underTest.getOccurrences(START_DATE, START_DATE, 3, null)).containsExactlyInAnyOrder(START_DATE, CURRENT_TIME, CURRENT_TIME.plusDays(1));
    }
}