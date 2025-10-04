package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.processing;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OneTimeEventOccurrenceProcessorTest {
    private static final LocalDate DATE = LocalDate.now();

    @InjectMocks
    private OneTimeEventOccurrenceProcessor underTest;

    @Mock
    private EventRequest eventRequest;

    @Mock
    private Event event;

    @Test
    void getRepetitionType() {
        assertThat(underTest.getRepetitionType()).isEqualTo(RepetitionType.ONE_TIME);
    }

    @Test
    void getEndDateFromRequest() {
        given(eventRequest.getStartDate()).willReturn(DATE);

        assertThat(underTest.getEndDate(eventRequest)).isEqualTo(DATE);
    }

    @Test
    void getEndDateFromEvent() {
        given(event.getStartDate()).willReturn(DATE);

        assertThat(underTest.getEndDate(event)).isEqualTo(DATE);
    }
}