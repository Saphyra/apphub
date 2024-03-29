package com.github.saphyra.apphub.service.calendar.service.occurrence.service.query;

import com.github.saphyra.apphub.service.calendar.dao.event.Event;
import com.github.saphyra.apphub.service.calendar.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class OneTimeEventHandlerTest {
    private static final LocalDate DATE = LocalDate.now();

    @Mock
    private RepeatedEventPostCollector repeatedEventPostCollector;

    @InjectMocks
    private OneTimeEventHandler underTest;

    @Mock
    private Occurrence occurrence1;

    @Mock
    private Occurrence occurrence2;

    @Mock
    private Event event;

    @Test
    public void handleOneTimeEvent_shouldReturn() {
        given(occurrence1.getDate()).willReturn(DATE);
        given(occurrence1.getType()).willReturn(OccurrenceType.DEFAULT);
        given(repeatedEventPostCollector.collect(event, List.of(occurrence1), List.of(DATE))).willReturn(List.of(occurrence2));

        List<Occurrence> result = underTest.handleOneTimeEvent(event, List.of(occurrence1), List.of(DATE));

        assertThat(result).containsExactly(occurrence2);
    }

    @Test
    public void handleOneTimeEvent_shouldNotReturn() {
        given(occurrence1.getType()).willReturn(OccurrenceType.DEFAULT);
        given(occurrence1.getDate()).willReturn(DATE.plusDays(1));

        List<Occurrence> result = underTest.handleOneTimeEvent(event, List.of(occurrence1), List.of(DATE));

        assertThat(result).isEmpty();
    }
}