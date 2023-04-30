package com.github.saphyra.apphub.service.calendar.service.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.CalendarResponse;
import com.github.saphyra.apphub.api.calendar.model.ReferenceDate;
import com.github.saphyra.apphub.service.calendar.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceStatus;
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
public class MarkOccurrenceDoneServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private CalendarQueryService calendarQueryService;

    @Mock
    private ReferenceDateValidator referenceDateValidator;

    @InjectMocks
    private MarkOccurrenceDoneService underTest;

    @Mock
    private ReferenceDate referenceDate;

    @Mock
    private Occurrence occurrence;

    @Mock
    private CalendarResponse calendarResponse;

    @Test
    public void markDone() {
        given(occurrenceDao.findByIdValidated(OCCURRENCE_ID)).willReturn(occurrence);
        given(calendarQueryService.getCalendar(USER_ID, referenceDate)).willReturn(List.of(calendarResponse));

        List<CalendarResponse> result = underTest.markDone(USER_ID, OCCURRENCE_ID, referenceDate);

        verify(referenceDateValidator).validate(referenceDate);
        verify(occurrence).setStatus(OccurrenceStatus.DONE);
        verify(occurrenceDao).save(occurrence);

        assertThat(result).containsExactly(calendarResponse);
    }
}