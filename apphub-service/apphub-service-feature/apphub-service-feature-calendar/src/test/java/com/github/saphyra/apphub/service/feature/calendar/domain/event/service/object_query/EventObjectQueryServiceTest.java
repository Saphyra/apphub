package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query;

import com.github.saphyra.apphub.service.feature.calendar.common.Operation;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EventObjectQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();

    @Mock
    private FindEventForOperationService findEventForOperationService;

    @Mock
    private EventsOfLabelQueryService eventsOfLabelQueryService;

    @Mock
    private GetEventsService getEventsService;

    @Mock
    private FindEventService findEventService;

    @Mock
    private GetLabellessEventsService getLabellessEventsService;

    @InjectMocks
    private EventObjectQueryService underTest;

    @Mock
    private Event event;

    @Mock
    private Event event2;

    @Test
    void findEvent_withOperation() {
        Operation operation = Operation.DELETE;
        given(findEventForOperationService.findEvent(USER_ID, EVENT_ID, operation)).willReturn(Optional.of(event));

        assertThat(underTest.findEvent(USER_ID, EVENT_ID, operation)).contains(event);
    }

    @Test
    void getEventsOfLabel() {
        given(eventsOfLabelQueryService.getEventsOfLabel(USER_ID, LABEL_ID)).willReturn(List.of(event));

        assertThat(underTest.getEventsOfLabel(USER_ID, LABEL_ID)).containsExactly(event);
    }

    @Test
    void getEvents() {
        given(getEventsService.getEvents(USER_ID)).willReturn(List.of(event, event2));

        assertThat(underTest.getEvents(USER_ID)).containsExactly(event, event2);
    }

    @Test
    void findEvent() {
        given(findEventService.findEvent(USER_ID, EVENT_ID)).willReturn(Optional.of(event));

        assertThat(underTest.findEvent(USER_ID, EVENT_ID)).contains(event);
    }

    @Test
    void getLabellessEvents() {
        given(getLabellessEventsService.getLabellessEvents(USER_ID)).willReturn(Stream.of(event, event2));

        assertThat(underTest.getLabellessEvents(USER_ID)).containsExactly(event, event2);
    }
}