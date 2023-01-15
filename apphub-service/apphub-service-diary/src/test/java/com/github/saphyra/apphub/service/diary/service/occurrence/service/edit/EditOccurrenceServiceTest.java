package com.github.saphyra.apphub.service.diary.service.occurrence.service.edit;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.api.diary.model.EditOccurrenceRequest;
import com.github.saphyra.apphub.api.diary.model.ReferenceDate;
import com.github.saphyra.apphub.service.diary.dao.event.Event;
import com.github.saphyra.apphub.service.diary.dao.event.EventDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceStatus;
import com.github.saphyra.apphub.service.diary.service.calendar.CalendarQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EditOccurrenceServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final LocalDate DATE = LocalDate.now();
    private static final String CONTENT = "content";
    private static final String NOTE = "note";
    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private EditOccurrenceRequestValidator editOccurrenceRequestValidator;

    @Mock
    private EventDao eventDao;

    @Mock
    private CalendarQueryService calendarQueryService;

    @InjectMocks
    private EditOccurrenceService underTest;

    @Mock
    private Occurrence occurrence;

    @Mock
    private Event event;

    @Mock
    private CalendarResponse calendarResponse;

    @Mock
    private ReferenceDate referenceDate;

    @Test
    public void editPending() {
        EditOccurrenceRequest request = EditOccurrenceRequest.builder()
            .referenceDate(referenceDate)
            .title(TITLE)
            .content(CONTENT)
            .note(NOTE)
            .build();

        given(occurrenceDao.findByIdValidated(OCCURRENCE_ID)).willReturn(occurrence);
        given(occurrence.getEventId()).willReturn(EVENT_ID);
        given(eventDao.findByIdValidated(EVENT_ID)).willReturn(event);
        given(occurrence.getStatus()).willReturn(OccurrenceStatus.DONE);

        given(calendarQueryService.getCalendar(USER_ID, referenceDate)).willReturn(List.of(calendarResponse));

        List<CalendarResponse> result = underTest.edit(USER_ID, OCCURRENCE_ID, request);

        verify(editOccurrenceRequestValidator).validate(request);

        verify(event).setTitle(TITLE);
        verify(event).setContent(CONTENT);
        verify(occurrence).setNote(NOTE);

        verify(eventDao).save(event);
        verify(occurrenceDao).save(occurrence);

        verify(occurrence, times(0)).setStatus(any());

        assertThat(result).containsExactly(calendarResponse);
    }

    @Test
    public void editVirtual() {
        EditOccurrenceRequest request = EditOccurrenceRequest.builder()
            .referenceDate(referenceDate)
            .title(TITLE)
            .content(CONTENT)
            .note(NOTE)
            .build();

        given(occurrenceDao.findByIdValidated(OCCURRENCE_ID)).willReturn(occurrence);
        given(occurrence.getEventId()).willReturn(EVENT_ID);
        given(eventDao.findByIdValidated(EVENT_ID)).willReturn(event);
        given(occurrence.getStatus()).willReturn(OccurrenceStatus.VIRTUAL);

        given(calendarQueryService.getCalendar(USER_ID, referenceDate)).willReturn(List.of(calendarResponse));

        List<CalendarResponse> result = underTest.edit(USER_ID, OCCURRENCE_ID, request);

        verify(editOccurrenceRequestValidator).validate(request);

        verify(event).setTitle(TITLE);
        verify(event).setContent(CONTENT);
        verify(occurrence).setNote(NOTE);

        verify(eventDao).save(event);
        verify(occurrenceDao).save(occurrence);

        verify(occurrence).setStatus(OccurrenceStatus.PENDING);

        assertThat(result).containsExactly(calendarResponse);
    }
}