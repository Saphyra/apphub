package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OccurrenceQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDate CURRENT_DATE = LocalDate.of(2026, 6, 20);
    private static final LocalDate START_DATE = CURRENT_DATE.minusDays(5);
    private static final LocalDate END_DATE = CURRENT_DATE.plusDays(5);
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private EventLabelMappingDao eventLabelMappingDao;

    @Mock
    private OccurrenceResponseMapper occurrenceResponseMapper;

    @Mock
    private EventDao eventDao;

    @Mock
    private OccurrenceQueryServiceHelper helper;

    @InjectMocks
    private OccurrenceQueryService underTest;

    @Mock
    private Occurrence occurrence;

    @Mock
    private OccurrenceResponse response;

    @Mock
    private Event event;

    @Test
    void getOccurrences() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(helper.getOccurrencesBetween(USER_ID, START_DATE, END_DATE)).willReturn(List.of(occurrence));
        given(occurrence.getEventId()).willReturn(EVENT_ID);
        given(eventDao.getByIds(USER_ID, Set.of(EVENT_ID))).willReturn(List.of(event));
        given(event.getEventId()).willReturn(EVENT_ID);
        given(eventLabelMappingDao.getLabelsOfEvents(USER_ID, Set.of(EVENT_ID))).willReturn(Map.of(EVENT_ID, List.of(LABEL_ID)));
        given(helper.getOccurrencesToAdd(event, occurrence, CURRENT_DATE, START_DATE, END_DATE)).willReturn(List.of(occurrence));
        given(occurrenceResponseMapper.toResponse(Map.of(EVENT_ID, event), List.of(occurrence))).willReturn(List.of(response));

        assertThat(underTest.getOccurrences(USER_ID, START_DATE, END_DATE, LABEL_ID)).containsExactly(response);
    }

    @Test
    void getOccurrences_filterByLabel() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(helper.getOccurrencesBetween(USER_ID, START_DATE, END_DATE)).willReturn(List.of(occurrence));
        given(occurrence.getEventId()).willReturn(EVENT_ID);
        given(eventDao.getByIds(USER_ID, Set.of(EVENT_ID))).willReturn(List.of(event));
        given(event.getEventId()).willReturn(EVENT_ID);
        given(eventLabelMappingDao.getLabelsOfEvents(USER_ID, Set.of(EVENT_ID))).willReturn(Map.of(EVENT_ID, List.of(UUID.randomUUID())));
        given(occurrenceResponseMapper.toResponse(Map.of(EVENT_ID, event), List.of())).willReturn(List.of());

        assertThat(underTest.getOccurrences(USER_ID, START_DATE, END_DATE, LABEL_ID)).isEmpty();
    }

    @Test
    void getOccurrencesOfEvent() {
        given(occurrenceDao.getByEventId(EVENT_ID)).willReturn(List.of(occurrence));
        given(occurrenceResponseMapper.toResponse(USER_ID, List.of(occurrence))).willReturn(List.of(response));

        assertThat(underTest.getOccurrencesOfEvent(USER_ID, EVENT_ID)).containsExactly(response);
    }

    @Test
    void getOccurrence() {
        given(occurrenceDao.findByIdValidated(EVENT_ID, OCCURRENCE_ID)).willReturn(occurrence);
        given(occurrenceResponseMapper.toResponse(USER_ID, occurrence)).willReturn(response);

        assertThat(underTest.getOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID)).isEqualTo(response);
    }
}