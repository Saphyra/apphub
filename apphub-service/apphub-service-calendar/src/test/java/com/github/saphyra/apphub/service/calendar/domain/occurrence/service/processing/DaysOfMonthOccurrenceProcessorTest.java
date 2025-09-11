package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.processing;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DaysOfMonthOccurrenceProcessorTest {
    @InjectMocks
    private DaysOfMonthOccurrenceProcessor underTest;

    @Test
    void getRepetitionType() {
        assertThat(underTest.getRepetitionType()).isEqualTo(RepetitionType.DAYS_OF_MONTH);
    }
}