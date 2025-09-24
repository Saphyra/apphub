package com.github.saphyra.apphub.service.calendar.domain.event;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.api.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.calendar.domain.event.service.CreateEventService;
import com.github.saphyra.apphub.service.calendar.domain.event.service.DeleteEventService;
import com.github.saphyra.apphub.service.calendar.domain.event.service.EditEventService;
import com.github.saphyra.apphub.service.calendar.domain.event.service.EventQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LABEL = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock
    private CreateEventService createEventService;

    @Mock
    private EventQueryService eventQueryService;

    @Mock
    private DeleteEventService deleteEventService;

    @Mock
    private EditEventService editEventService;

    @InjectMocks
    private EventControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private EventRequest request;

    @Mock
    private EventResponse eventResponse;

    @Test
    void createEvent() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(createEventService.create(USER_ID, request)).willReturn(EVENT_ID);

        assertThat(underTest.createEvent(request, accessTokenHeader).getValue()).isEqualTo(EVENT_ID);
    }

    @Test
    void getEvents() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(eventQueryService.getEvents(USER_ID, LABEL)).willReturn(List.of(eventResponse));

        assertThat(underTest.getEvents(LABEL, accessTokenHeader)).containsExactly(eventResponse);
    }

    @Test
    void getEvent() {
        given(eventQueryService.getEvent(EVENT_ID)).willReturn(eventResponse);

        assertThat(underTest.getEvent(EVENT_ID, accessTokenHeader)).isEqualTo(eventResponse);
    }

    @Test
    void deleteEvent() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.deleteEvent(EVENT_ID, accessTokenHeader);

        then(deleteEventService).should().delete(USER_ID, EVENT_ID);
    }

    @Test
    void editEvent() {
        underTest.editEvent(request, EVENT_ID, accessTokenHeader);

        then(editEventService).should().edit(EVENT_ID, request);
    }
}