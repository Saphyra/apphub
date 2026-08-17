package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query.EventObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EventQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();

    @Mock
    private EventResponseMapper eventResponseMapper;

    @Mock
    private EventObjectQueryService eventObjectQueryService;

    @InjectMocks
    private EventQueryService underTest;

    @Mock
    private Event event;

    @Mock
    private EventResponse eventResponse;

    @Test
    void getEvents_nullLabelId() {
        given(eventObjectQueryService.getEvents(USER_ID)).willReturn(List.of(event));
        given(eventResponseMapper.toResponse(USER_ID, List.of(event))).willReturn(List.of(eventResponse));

        assertThat(underTest.getEvents(USER_ID, null)).containsExactly(eventResponse);
    }

    @Test
    void getEvents_withLabelId() {
        given(eventObjectQueryService.getEventsOfLabel(USER_ID, LABEL_ID)).willReturn(List.of(event));
        given(eventResponseMapper.toResponse(USER_ID, List.of(event))).willReturn(List.of(eventResponse));

        assertThat(underTest.getEvents(USER_ID, LABEL_ID)).containsExactly(eventResponse);
    }

    @Test
    void getLabellessEvents() {
        given(eventObjectQueryService.getLabellessEvents(USER_ID)).willReturn(Stream.of(event));
        given(eventResponseMapper.toResponse(USER_ID, List.of(event))).willReturn(List.of(eventResponse));

        assertThat(underTest.getLabellessEvents(USER_ID)).containsExactly(eventResponse);
    }

    @Test
    void getEvent_found() {
        given(eventObjectQueryService.findEvent(USER_ID, LABEL_ID)).willReturn(java.util.Optional.of(event));
        given(eventResponseMapper.toResponse(USER_ID, event)).willReturn(eventResponse);

        assertThat(underTest.getEvent(USER_ID, LABEL_ID)).isEqualTo(eventResponse);
    }

    @Test
    void getEvent_notFound() {
        given(eventObjectQueryService.findEvent(USER_ID, LABEL_ID)).willReturn(java.util.Optional.empty());

        ExceptionValidator.validateNotFoundException(() -> underTest.getEvent(USER_ID, LABEL_ID));
    }
}