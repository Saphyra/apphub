package com.github.saphyra.apphub.service.calendar.service.occurrence.service.query;

import com.github.saphyra.apphub.service.calendar.dao.event.Event;
import com.github.saphyra.apphub.service.calendar.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class DateOfLastOccurrenceProviderTest {
    private static final LocalDate DATE = LocalDate.now();

    @InjectMocks
    private DateOfLastOccurrenceProvider underTest;

    @Mock
    private Event event;

    @Mock
    private Occurrence virtualOccurrence;

    @Mock
    private Occurrence futureOccurrence;

    @Mock
    private Occurrence pastOccurrence;

    @Mock
    private Occurrence lastOccurrence;

    @Test
    public void getDateOfLastOccurrence() {
        given(virtualOccurrence.getStatus()).willReturn(OccurrenceStatus.VIRTUAL);
        given(futureOccurrence.getStatus()).willReturn(OccurrenceStatus.PENDING);
        given(pastOccurrence.getStatus()).willReturn(OccurrenceStatus.PENDING);
        given(lastOccurrence.getStatus()).willReturn(OccurrenceStatus.PENDING);

        given(futureOccurrence.getDate()).willReturn(DATE.plusDays(1));
        given(pastOccurrence.getDate()).willReturn(DATE.minusDays(2));
        given(lastOccurrence.getDate()).willReturn(DATE.minusDays(1));

        LocalDate result = underTest.getDateOfLastOccurrence(List.of(virtualOccurrence, futureOccurrence, pastOccurrence, lastOccurrence), event, DATE);

        assertThat(result).isEqualTo(DATE.minusDays(1));
    }

    @Test
    public void getDateOfLastOccurrence_occurrenceNotFound() {
        given(event.getStartDate()).willReturn(DATE);

        LocalDate result = underTest.getDateOfLastOccurrence(Collections.emptyList(), event, DATE);

        assertThat(result).isEqualTo(DATE);
    }
}