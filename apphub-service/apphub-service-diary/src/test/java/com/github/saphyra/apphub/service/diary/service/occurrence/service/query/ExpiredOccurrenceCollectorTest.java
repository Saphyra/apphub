package com.github.saphyra.apphub.service.diary.service.occurrence.service.query;

import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ExpiredOccurrenceCollectorTest {
    private static final UUID EVENT_ID_1 = UUID.randomUUID();
    private static final UUID EVENT_ID_2 = UUID.randomUUID();
    private static final LocalDate CURRENT_DATE = LocalDate.now();

    private final ExpiredOccurrenceCollector underTest = new ExpiredOccurrenceCollector();

    @Mock
    private Occurrence occurrence1;

    @Mock
    private Occurrence occurrence2;

    @Mock
    private Occurrence occurrence3;

    @Test
    public void getExpiredOccurrences() {
        given(occurrence1.getEventId()).willReturn(EVENT_ID_1);
        given(occurrence2.getEventId()).willReturn(EVENT_ID_1);
        given(occurrence3.getEventId()).willReturn(EVENT_ID_2);

        given(occurrence1.getStatus()).willReturn(OccurrenceStatus.EXPIRED);
        given(occurrence3.getStatus()).willReturn(OccurrenceStatus.VIRTUAL);

        given(occurrence1.getDate()).willReturn(CURRENT_DATE.minusDays(2));
        given(occurrence2.getDate()).willReturn(CURRENT_DATE.minusDays(1));

        Collection<Occurrence> result = underTest.getExpiredOccurrences(List.of(occurrence1, occurrence2, occurrence3));

        assertThat(result).containsExactly(occurrence1);
    }
}