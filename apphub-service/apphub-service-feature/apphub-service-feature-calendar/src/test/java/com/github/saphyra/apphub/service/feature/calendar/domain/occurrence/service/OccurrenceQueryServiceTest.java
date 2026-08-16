package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.feature.calendar.domain.OccurrenceObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
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
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalDate START_DATE = CURRENT_DATE.minusDays(1);
    private static final LocalDate END_DATE = CURRENT_DATE.plusDays(2);
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private EventLabelMappingDao eventLabelMappingDao;

    @Mock
    private OccurrenceResponseMapper occurrenceResponseMapper;

    @Mock
    private OccurrenceQueryServiceHelper helper;

    @Mock
    private OccurrenceObjectQueryService occurrenceObjectQueryService;

    @InjectMocks
    private OccurrenceQueryService underTest;

    @Mock
    private Occurrence occurrence;

    @Mock
    private Event event;

    @Mock
    private EventLabelMapping eventLabelMapping;

    @Mock
    private OccurrenceResponse occurrenceResponse;

    @Test
    void getOccurrences() {
        given(occurrenceObjectQueryService.getOccurrences(USER_ID)).willReturn(Map.of(event, List.of(occurrence)));
        given(event.getEventId()).willReturn(EVENT_ID);
        given(eventLabelMappingDao.getLabelsOfEvents(USER_ID, List.of(EVENT_ID))).willReturn(List.of(eventLabelMapping));
        given(eventLabelMapping.getEventId()).willReturn(EVENT_ID);
        given(eventLabelMapping.getLabelIds()).willReturn(Map.of(LABEL_ID, USER_ID));
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(helper.getOccurrencesToAdd(event, occurrence, CURRENT_DATE, START_DATE, END_DATE)).willReturn(List.of(occurrence));
        given(occurrenceResponseMapper.toResponse(USER_ID, Set.of(event), List.of(occurrence))).willReturn(List.of(occurrenceResponse));

        assertThat(underTest.getOccurrences(USER_ID, START_DATE, END_DATE, LABEL_ID)).containsExactly(occurrenceResponse);
    }

    @Test
    void getOccurrencesOfEvent() {
        given(occurrenceObjectQueryService.getOccurrences(USER_ID, EVENT_ID)).willReturn(new BiWrapper<>(event, List.of(occurrence)));
        given(occurrenceResponseMapper.toResponse(USER_ID, event, List.of(occurrence))).willReturn(List.of(occurrenceResponse));

        assertThat(underTest.getOccurrencesOfEvent(USER_ID, EVENT_ID)).containsExactly(occurrenceResponse);
    }

    @Test
    void getOccurrence(){
        given(occurrenceObjectQueryService.findOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID)).willReturn(new BiWrapper<>(event, occurrence));
        given(occurrenceResponseMapper.toResponse(USER_ID, event, occurrence)).willReturn(occurrenceResponse);

        assertThat(underTest.getOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID)).isEqualTo(occurrenceResponse);
    }
}