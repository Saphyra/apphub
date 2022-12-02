package com.github.saphyra.apphub.service.diary.service.occurrence.service.query;

import com.github.saphyra.apphub.service.diary.dao.event.Event;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceType;
import com.github.saphyra.apphub.service.diary.service.occurrence.service.OccurrenceFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RepeatedEventPostCollectorTest {
    private static final LocalDate START_DATE = LocalDate.now();
    private static final LocalDate DATE_1 = START_DATE.plusDays(1);
    private static final LocalDate DATE_2 = DATE_1.plusDays(1);

    @Mock
    private OccurrenceFactory occurrenceFactory;

    @Mock
    private OccurrenceDao occurrenceDao;

    @InjectMocks
    private RepeatedEventPostCollector underTest;

    @Mock
    private Event event;

    @Mock
    private Occurrence defaultOccurrence;

    @Mock
    private Occurrence followUpOccurrence;

    @Mock
    private Occurrence newOccurrence;

    @Test
    public void collect() {
        given(event.getRepeat()).willReturn(3);

        given(defaultOccurrence.getType()).willReturn(OccurrenceType.DEFAULT);
        given(defaultOccurrence.getDate()).willReturn(START_DATE);

        given(followUpOccurrence.getType()).willReturn(OccurrenceType.FOLLOW_UP);
        given(followUpOccurrence.getDate()).willReturn(DATE_1);

        given(occurrenceFactory.createVirtual(DATE_2, event, OccurrenceType.FOLLOW_UP)).willReturn(newOccurrence);

        List<Occurrence> result = underTest.collect(event, List.of(defaultOccurrence, followUpOccurrence), List.of(START_DATE, DATE_1, DATE_2));

        assertThat(result).containsExactlyInAnyOrder(defaultOccurrence, followUpOccurrence, newOccurrence);

        verify(occurrenceDao).save(newOccurrence);
    }
}