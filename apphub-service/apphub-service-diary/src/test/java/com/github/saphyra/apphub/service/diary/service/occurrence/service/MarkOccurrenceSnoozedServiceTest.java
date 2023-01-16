package com.github.saphyra.apphub.service.diary.service.occurrence.service;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.api.diary.model.ReferenceDate;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceStatus;
import com.github.saphyra.apphub.service.diary.service.ReferenceDateValidator;
import com.github.saphyra.apphub.service.diary.service.calendar.CalendarQueryService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MarkOccurrenceSnoozedServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private CalendarQueryService calendarQueryService;

    @Mock
    private ReferenceDateValidator referenceDateValidator;

    @InjectMocks
    private MarkOccurrenceSnoozedService underTest;

    @Mock
    private ReferenceDate referenceDate;

    @Mock
    private Occurrence occurrence;

    @Mock
    private CalendarResponse calendarResponse;

    @Test
    public void markSnoozed_alreadyDone() {
        given(occurrenceDao.findByIdValidated(OCCURRENCE_ID)).willReturn(occurrence);
        given(occurrence.getStatus()).willReturn(OccurrenceStatus.DONE);

        Throwable ex = catchThrowable(() -> underTest.markSnoozed(USER_ID, OCCURRENCE_ID, referenceDate));

        verify(referenceDateValidator).validate(referenceDate);

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.INVALID_STATUS);
    }

    @Test
    public void markSnoozed() {
        given(occurrenceDao.findByIdValidated(OCCURRENCE_ID)).willReturn(occurrence);
        given(occurrence.getStatus()).willReturn(OccurrenceStatus.PENDING);
        given(calendarQueryService.getCalendar(USER_ID, referenceDate)).willReturn(List.of(calendarResponse));

        List<CalendarResponse> result = underTest.markSnoozed(USER_ID, OCCURRENCE_ID, referenceDate);

        verify(referenceDateValidator).validate(referenceDate);
        verify(occurrence).setStatus(OccurrenceStatus.SNOOZED);
        verify(occurrenceDao).save(occurrence);

        assertThat(result).containsExactly(calendarResponse);
    }
}