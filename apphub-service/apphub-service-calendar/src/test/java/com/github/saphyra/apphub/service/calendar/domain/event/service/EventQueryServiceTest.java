package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.github.saphyra.apphub.api.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.service.EventLabelMappingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EventQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock
    private EventDao eventDao;

    @Mock
    private EventLabelMappingService eventLabelMappingService;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventQueryService underTest;

    @Mock
    private Event event;

    @Mock
    private EventResponse eventResponse;

    @Test
    void getEvents_noFilter() {
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(event));
        given(eventMapper.toResponse(event)).willReturn(eventResponse);

        assertThat(underTest.getEvents(USER_ID, null)).containsExactly(eventResponse);
    }

    @Test
    void getEvents_filter() {
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(event, event));
        given(eventLabelMappingService.hasLabel(EVENT_ID, LABEL_ID)).willReturn(true, false);
        given(event.getEventId()).willReturn(EVENT_ID);
        given(eventMapper.toResponse(event)).willReturn(eventResponse);

        assertThat(underTest.getEvents(USER_ID, LABEL_ID)).containsExactly(eventResponse);
    }

    @Test
    void getEvents() {
        given(eventDao.findByIdValidated(EVENT_ID)).willReturn(event);
        given(eventMapper.toResponse(event)).willReturn(eventResponse);

        assertThat(underTest.getEvent(EVENT_ID)).isEqualTo(eventResponse);
    }

    @Test
    void getLabellessEvents_hasLabel() {
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(event, event));
        given(eventLabelMappingService.getLabelIds(EVENT_ID)).willReturn(List.of(UUID.randomUUID()));
        given(event.getEventId()).willReturn(EVENT_ID);

        assertThat(underTest.getLabellessEvents(USER_ID)).isEmpty();
    }

    @Test
    void getLabellessEvents_hasNoLabel() {
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(event, event));
        given(eventLabelMappingService.getLabelIds(EVENT_ID)).willReturn(List.of());
        given(event.getEventId()).willReturn(EVENT_ID);
        given(eventMapper.toResponse(event)).willReturn(eventResponse);

        assertThat(underTest.getLabellessEvents(USER_ID)).contains(eventResponse);
    }
}