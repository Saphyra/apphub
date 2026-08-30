package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.api.feature.calendar.model.request.OccurrenceRequest;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query.EventObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CreateOccurrenceServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final LocalDate DATE = LocalDate.now();
    private static final LocalTime TIME = LocalTime.now();
    private static final Integer REMIND_ME_BEFORE_DAYS = 1;
    private static final String NOTE = "note";
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();

    @Mock
    private OccurrenceCreator occurrenceCreator;

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private OccurrenceFactory occurrenceFactory;

    @Mock
    private OccurrenceRequestValidator occurrenceRequestValidator;

    @Mock
    private EventObjectQueryService eventObjectQueryService;

    private CreateOccurrenceService underTest;

    @Mock
    private EventRequest eventRequest;

    @Mock
    private OccurrenceRequest occurrenceRequest;

    @Mock
    private Occurrence occurrence;

    @Mock
    private Event event;

    @BeforeEach
    void setUp() {
        underTest = CreateOccurrenceService.builder()
            .occurrenceCreators(List.of(occurrenceCreator))
            .occurrenceDao(occurrenceDao)
            .occurrenceFactory(occurrenceFactory)
            .occurrenceRequestValidator(occurrenceRequestValidator)
            .eventObjectQueryService(eventObjectQueryService)
            .build();
    }

    @Test
    void createOccurrences_noOccurrenceCreator() {
        given(occurrenceCreator.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);
        given(eventRequest.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);

        assertThat(catchThrowable(() -> underTest.createOccurrences(USER_ID, EVENT_ID, eventRequest)))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void createOccurrences() {
        given(occurrenceCreator.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);
        given(eventRequest.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);

        underTest.createOccurrences(USER_ID, EVENT_ID, eventRequest);

        then(occurrenceCreator).should().createOccurrences(USER_ID, EVENT_ID, eventRequest);
    }

    @Test
    void createOccurrence() {
        given(occurrenceRequest.getDate()).willReturn(DATE);
        given(occurrenceRequest.getTime()).willReturn(TIME);
        given(occurrenceRequest.getRemindMeBeforeDays()).willReturn(REMIND_ME_BEFORE_DAYS);
        given(occurrenceRequest.getNote()).willReturn(NOTE);
        given(occurrenceFactory.create(USER_ID, EVENT_ID, DATE, TIME, REMIND_ME_BEFORE_DAYS, NOTE, false)).willReturn(occurrence);
        given(occurrence.getOccurrenceId()).willReturn(OCCURRENCE_ID);
        given(eventObjectQueryService.findEvent(USER_ID, EVENT_ID)).willReturn(Optional.of(event));

        assertThat(underTest.createOccurrence(USER_ID, EVENT_ID, occurrenceRequest)).isEqualTo(OCCURRENCE_ID);

        then(occurrenceRequestValidator).should().validate(occurrenceRequest);
        then(occurrenceDao).should().save(occurrence);
    }

    @Test
    void createOccurrence_noEvent() {
        given(eventObjectQueryService.findEvent(USER_ID, EVENT_ID)).willReturn(Optional.empty());

        ExceptionValidator.validateNotFoundException(() -> underTest.createOccurrence(USER_ID, EVENT_ID, occurrenceRequest));

        then(occurrenceRequestValidator).should().validate(occurrenceRequest);
    }
}