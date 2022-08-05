package com.github.saphyra.apphub.service.diary.service.occurrence.service.query;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.diary.dao.event.Event;
import com.github.saphyra.apphub.service.diary.dao.event.EventDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OccurrenceQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalDate DATE_1 = CURRENT_DATE.minusDays(1);
    private static final LocalDate DATE_2 = CURRENT_DATE.plusDays(1);
    private static final LocalDate DATE_3 = DATE_2.plusDays(1);
    public static final List<LocalDate> SORTED_DATES = List.of(DATE_1, CURRENT_DATE, DATE_2, DATE_3);

    @Mock
    private OccurrenceFetcher occurrenceFetcher;

    @Mock
    private EventDao eventDao;

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private ExpiredOccurrenceCollector expiredOccurrenceCollector;

    @InjectMocks
    private OccurrenceQueryService underTest;

    @Mock
    private Event event;

    @Mock
    private Occurrence occurrence1;

    @Mock
    private Occurrence occurrence2;

    @Mock
    private Occurrence expiredOccurrence;

    @Test
    public void getOccurrences() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(event));
        given(occurrenceFetcher.fetchOccurrencesOfEvent(event, SORTED_DATES)).willReturn(List.of(occurrence1, occurrence2));

        given(occurrence1.getDate()).willReturn(DATE_1);
        given(occurrence2.getDate()).willReturn(DATE_2);
        given(occurrence1.getStatus()).willReturn(OccurrenceStatus.VIRTUAL);

        given(expiredOccurrenceCollector.getExpiredOccurrences(List.of(occurrence1, occurrence2))).willReturn(List.of(expiredOccurrence));

        Map<LocalDate, List<Occurrence>> result = underTest.getOccurrences(USER_ID, List.of(DATE_2, DATE_1, DATE_3, CURRENT_DATE));

        verify(occurrence1).setStatus(OccurrenceStatus.EXPIRED);
        verify(occurrenceDao).save(occurrence1);

        assertThat(result).hasSize(4);
        assertThat(result).containsEntry(DATE_1, List.of(occurrence1));
        assertThat(result).containsEntry(CURRENT_DATE, List.of(expiredOccurrence));
        assertThat(result).containsEntry(DATE_2, List.of(occurrence2));
        assertThat(result).containsEntry(DATE_3, Collections.emptyList());
    }
}