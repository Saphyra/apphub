package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.feature.calendar.model.request.OccurrenceRequest;
import com.github.saphyra.apphub.api.feature.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.OccurrenceObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.Operation;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditOccurrenceServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final LocalDate DATE = LocalDate.now();
    private static final LocalTime TIME = LocalTime.now();
    private static final String NOTE = "note";
    private static final Integer REMIND_ME_BEFORE_DAYS = 3;

    @Mock
    private OccurrenceRequestValidator occurrenceRequestValidator;

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private OccurrenceResponseMapper occurrenceResponseMapper;

    @Mock
    private OccurrenceObjectQueryService occurrenceObjectQueryService;

    @InjectMocks
    private EditOccurrenceService underTest;

    @Mock
    private Event event;

    @Mock
    private Occurrence occurrence;

    @Mock
    private OccurrenceResponse response;

    @Test
    void editOccurrence() {
        OccurrenceRequest request = OccurrenceRequest.builder()
            .date(DATE)
            .time(TIME)
            .status(OccurrenceStatus.DONE)
            .note(NOTE)
            .remindMeBeforeDays(REMIND_ME_BEFORE_DAYS)
            .reminded(true)
            .autoDone(true)
            .build();

        given(occurrenceObjectQueryService.findOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID, Operation.EDIT)).willReturn(new BiWrapper<>(event, occurrence));

        underTest.editOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID, request);

        then(occurrenceRequestValidator).should().validate(request);

        then(occurrence).should().setDate(DATE);
        then(occurrence).should().setTime(TIME);
        then(occurrence).should().setStatus(OccurrenceStatus.DONE);
        then(occurrence).should().setNote(NOTE);
        then(occurrence).should().setRemindMeBeforeDays(REMIND_ME_BEFORE_DAYS);
        then(occurrence).should().setReminded(true);
        then(occurrence).should().setAutoDone(true);

        then(occurrenceDao).should().save(occurrence);
    }

    @Test
    void editOccurrence_inheritFromEvent() {
        OccurrenceRequest request = OccurrenceRequest.builder()
            .date(DATE)
            .time(TIME)
            .status(OccurrenceStatus.DONE)
            .note(NOTE)
            .remindMeBeforeDays(REMIND_ME_BEFORE_DAYS)
            .reminded(true)
            .autoDone(true)
            .build();

        given(occurrenceObjectQueryService.findOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID, Operation.EDIT)).willReturn(new BiWrapper<>(event, occurrence));
        given(event.getTime()).willReturn(TIME);
        given(event.getRemindMeBeforeDays()).willReturn(REMIND_ME_BEFORE_DAYS);
        given(event.isAutoDone()).willReturn(true);

        underTest.editOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID, request);

        then(occurrenceRequestValidator).should().validate(request);

        then(occurrence).should().setDate(DATE);
        then(occurrence).should().setTime(null);
        then(occurrence).should().setStatus(OccurrenceStatus.DONE);
        then(occurrence).should().setNote(NOTE);
        then(occurrence).should().setRemindMeBeforeDays(null);
        then(occurrence).should().setReminded(true);
        then(occurrence).should().setAutoDone(null);

        then(occurrenceDao).should().save(occurrence);
    }

    @Test
    void editOccurrenceStatus() {
        given(occurrenceObjectQueryService.findOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID, Operation.EDIT)).willReturn(new BiWrapper<>(event, occurrence));
        given(occurrenceObjectQueryService.findOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID)).willReturn(new BiWrapper<>(event, occurrence));
        given(occurrenceResponseMapper.toResponse(USER_ID, event, occurrence)).willReturn(response);

        assertThat(underTest.editOccurrenceStatus(USER_ID, EVENT_ID, OCCURRENCE_ID, OccurrenceStatus.DONE)).isEqualTo(response);

        then(occurrence).should().setStatus(OccurrenceStatus.DONE);
        then(occurrenceDao).should().save(occurrence);
    }

    @Test
    void setReminded() {
        given(occurrenceObjectQueryService.findOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID, Operation.EDIT)).willReturn(new BiWrapper<>(event, occurrence));
        given(occurrenceObjectQueryService.findOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID)).willReturn(new BiWrapper<>(event, occurrence));
        given(occurrenceResponseMapper.toResponse(USER_ID, event, occurrence)).willReturn(response);

        assertThat(underTest.setReminded(USER_ID, EVENT_ID, OCCURRENCE_ID)).isEqualTo(response);

        then(occurrence).should().setReminded(true);
        then(occurrenceDao).should().save(occurrence);
    }
}