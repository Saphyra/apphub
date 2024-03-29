package com.github.saphyra.apphub.service.calendar.service.event.service;

import com.github.saphyra.apphub.api.calendar.model.CalendarResponse;
import com.github.saphyra.apphub.api.calendar.model.ReferenceDate;
import com.github.saphyra.apphub.service.calendar.dao.event.EventDao;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar.service.ReferenceDateValidator;
import com.github.saphyra.apphub.service.calendar.service.calendar.CalendarQueryService;
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
public class DeleteEventServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private EventDao eventDao;

    @Mock
    private ReferenceDateValidator referenceDateValidator;

    @Mock
    private CalendarQueryService calendarQueryService;

    @InjectMocks
    private DeleteEventService underTest;

    @Mock
    private CalendarResponse calendarResponse;

    @Mock
    private ReferenceDate referenceDate;

    @Test
    public void deleteEvent() {
        given(calendarQueryService.getCalendar(USER_ID, referenceDate)).willReturn(List.of(calendarResponse));

        List<CalendarResponse> result = underTest.delete(USER_ID, EVENT_ID, referenceDate);

        assertThat(result).containsExactly(calendarResponse);
        verify(eventDao).deleteById(EVENT_ID);
        verify(occurrenceDao).deleteByEventId(EVENT_ID);
        verify(referenceDateValidator).validate(referenceDate);
    }
}