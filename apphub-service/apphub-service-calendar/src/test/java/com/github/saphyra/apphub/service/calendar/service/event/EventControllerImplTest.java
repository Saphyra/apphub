package com.github.saphyra.apphub.service.calendar.service.event;

import com.github.saphyra.apphub.api.calendar.model.CalendarResponse;
import com.github.saphyra.apphub.api.calendar.model.CreateEventRequest;
import com.github.saphyra.apphub.api.calendar.model.ReferenceDate;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.calendar.service.event.service.DeleteEventService;
import com.github.saphyra.apphub.service.calendar.service.event.service.creation.CreateEventService;
import org.junit.jupiter.api.BeforeEach;
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
public class EventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock
    private CreateEventService createEventService;

    @Mock
    private DeleteEventService deleteEventService;

    @InjectMocks
    private EventControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private CreateEventRequest createEventRequest;

    @Mock
    private ReferenceDate referenceDate;

    @Mock
    private CalendarResponse calendarResponse;

    @BeforeEach
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void createEvent() {
        given(createEventService.createEvent(USER_ID, createEventRequest)).willReturn(List.of(calendarResponse));

        List<CalendarResponse> result = underTest.createEvent(createEventRequest, accessTokenHeader);

        assertThat(result).containsExactly(calendarResponse);
    }

    @Test
    public void deleteEvent() {
        given(deleteEventService.delete(USER_ID, EVENT_ID, referenceDate)).willReturn(List.of(calendarResponse));

        List<CalendarResponse> result = underTest.deleteEvent(referenceDate, EVENT_ID, accessTokenHeader);

        assertThat(result).containsExactly(calendarResponse);
    }
}