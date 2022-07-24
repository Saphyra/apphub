package com.github.saphyra.apphub.service.diary.service.event.service;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.api.diary.model.ReferenceDate;
import com.github.saphyra.apphub.service.diary.dao.event.EventDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.diary.service.ReferenceDateValidator;
import com.github.saphyra.apphub.service.diary.service.calendar.CalendarQueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
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