package com.github.saphyra.apphub.service.diary.service.occurrence.service.query;

import com.github.saphyra.apphub.api.diary.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.service.diary.dao.event.Event;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class OccurrenceFetcherTest {
    private static final LocalDate DATE = LocalDate.now();
    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private OneTimeEventHandler oneTimeEventHandler;

    @Mock
    private DaysOfWeekEventHandler daysOfWeekEventHandler;

    @Mock
    private EveryXDayEventHandler everyXDayEventHandler;

    @Mock
    private DaysOfMonthEventHandler daysOfMonthEventHandler;

    @InjectMocks
    private OccurrenceFetcher underTest;

    @Mock
    private Event event;

    @Mock
    private Occurrence occurrence;

    @Test
    public void fetchOccurrencesOfEvent_oneTimeEvent() {
        given(event.getEventId()).willReturn(EVENT_ID);
        given(event.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);
        given(occurrenceDao.getByEventId(EVENT_ID)).willReturn(List.of(occurrence));
        given(occurrence.getDate()).willReturn(DATE);

        given(oneTimeEventHandler.handleOneTimeEvent(event, List.of(occurrence), List.of(DATE))).willReturn(List.of(occurrence));

        List<Occurrence> result = underTest.fetchOccurrencesOfEvent(event, List.of(DATE));

        assertThat(result).containsExactly(occurrence);
    }

    @Test
    public void fetchOccurrencesOfEvent_daysOfWeekEvent() {
        given(event.getEventId()).willReturn(EVENT_ID);
        given(event.getRepetitionType()).willReturn(RepetitionType.DAYS_OF_WEEK);
        given(occurrenceDao.getByEventId(EVENT_ID)).willReturn(List.of(occurrence));
        given(occurrence.getDate()).willReturn(DATE);

        given(daysOfWeekEventHandler.handleDaysOfWeekEvent(event, List.of(DATE), CollectionUtils.singleValueMap(DATE, occurrence, new OptionalHashMap<>()))).willReturn(List.of(occurrence));

        List<Occurrence> result = underTest.fetchOccurrencesOfEvent(event, List.of(DATE));

        assertThat(result).containsExactly(occurrence);
    }

    @Test
    public void fetchOccurrencesOfEvent_everyXDaysEvent() {
        given(event.getEventId()).willReturn(EVENT_ID);
        given(event.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);
        given(occurrenceDao.getByEventId(EVENT_ID)).willReturn(List.of(occurrence));
        given(occurrence.getDate()).willReturn(DATE);

        given(everyXDayEventHandler.handleEveryXDayEvent(event, List.of(DATE), CollectionUtils.singleValueMap(DATE, occurrence, new OptionalHashMap<>()))).willReturn(List.of(occurrence));

        List<Occurrence> result = underTest.fetchOccurrencesOfEvent(event, List.of(DATE));

        assertThat(result).containsExactly(occurrence);
    }

    @Test
    public void fetchOccurrencesOfEvent_daysOfMonthEvent() {
        given(event.getEventId()).willReturn(EVENT_ID);
        given(event.getRepetitionType()).willReturn(RepetitionType.DAYS_OF_MONTH);
        given(occurrenceDao.getByEventId(EVENT_ID)).willReturn(List.of(occurrence));
        given(occurrence.getDate()).willReturn(DATE);

        given(daysOfMonthEventHandler.handleDaysOfMonthEvent(event, List.of(DATE), CollectionUtils.singleValueMap(DATE, occurrence, new OptionalHashMap<>()))).willReturn(List.of(occurrence));

        List<Occurrence> result = underTest.fetchOccurrencesOfEvent(event, List.of(DATE));

        assertThat(result).containsExactly(occurrence);
    }
}