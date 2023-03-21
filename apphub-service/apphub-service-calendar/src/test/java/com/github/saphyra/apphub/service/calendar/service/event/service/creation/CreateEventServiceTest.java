package com.github.saphyra.apphub.service.calendar.service.event.service.creation;

import com.github.saphyra.apphub.api.calendar.model.CalendarResponse;
import com.github.saphyra.apphub.api.calendar.model.CreateEventRequest;
import com.github.saphyra.apphub.api.calendar.model.ReferenceDate;
import com.github.saphyra.apphub.service.calendar.dao.event.Event;
import com.github.saphyra.apphub.service.calendar.dao.event.EventDao;
import com.github.saphyra.apphub.service.calendar.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar.service.calendar.CalendarQueryService;
import com.github.saphyra.apphub.service.calendar.service.occurrence.service.OccurrenceFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CreateEventServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private CreateEventRequestValidator createEventRequestValidator;

    @Mock
    private EventFactory eventFactory;

    @Mock
    private EventDao eventDao;

    @Mock
    private OccurrenceFactory occurrenceFactory;

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private CalendarQueryService calendarQueryService;

    @InjectMocks
    private CreateEventService underTest;

    @Mock
    private CreateEventRequest request;

    @Mock
    private Event event;

    @Mock
    private Occurrence occurrence;

    @Mock
    private CalendarResponse calendarResponse;

    @Mock
    private ReferenceDate referenceDate;

    @Test
    public void createEvent() {
        given(eventFactory.create(USER_ID, request)).willReturn(event);
        given(occurrenceFactory.createPending(event)).willReturn(occurrence);
        given(request.getReferenceDate()).willReturn(referenceDate);
        given(calendarQueryService.getCalendar(USER_ID, referenceDate)).willReturn(List.of(calendarResponse));

        List<CalendarResponse> result = underTest.createEvent(USER_ID, request);

        verify(createEventRequestValidator).validate(request);
        verify(eventDao).save(event);
        verify(occurrenceDao).save(occurrence);
        assertThat(result).containsExactly(calendarResponse);
    }
}