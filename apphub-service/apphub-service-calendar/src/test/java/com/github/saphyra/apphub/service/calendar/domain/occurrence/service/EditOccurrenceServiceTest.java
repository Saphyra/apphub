package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.calendar.model.request.OccurrenceRequest;
import com.github.saphyra.apphub.api.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
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
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final LocalDate DATE = LocalDate.now();
    private static final LocalTime TIME = LocalTime.now();
    private static final String NOTE = "note";
    private static final Integer REMIND_ME_BEFORE_DAYS = 3;


    @Mock
    private OccurrenceRequestValidator occurrenceRequestValidator;

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private OccurrenceMapper occurrenceMapper;

    @Mock
    private EventDao eventDao;

    @InjectMocks
    private EditOccurrenceService underTest;

    @Mock
    private OccurrenceRequest occurrenceRequest;

    @Mock
    private Event event;

    @Mock
    private Occurrence occurrence;

    @Mock
    private OccurrenceResponse occurrenceResponse;

    @Test
    void editOccurrence_newValues() {
        given(occurrenceDao.findByIdValidated(OCCURRENCE_ID)).willReturn(occurrence);
        given(occurrence.getEventId()).willReturn(EVENT_ID);
        given(eventDao.findByIdValidated(EVENT_ID)).willReturn(event);

        given(occurrenceRequest.getDate()).willReturn(DATE);
        given(occurrenceRequest.getTime()).willReturn(TIME);
        given(occurrenceRequest.getStatus()).willReturn(OccurrenceStatus.DONE);
        given(occurrenceRequest.getNote()).willReturn(NOTE);
        given(occurrenceRequest.getRemindMeBeforeDays()).willReturn(REMIND_ME_BEFORE_DAYS);
        given(occurrenceRequest.getReminded()).willReturn(true);
        given(event.getTime()).willReturn(TIME.plusHours(1));
        given(event.getRemindMeBeforeDays()).willReturn(REMIND_ME_BEFORE_DAYS + 1);

        underTest.editOccurrence(OCCURRENCE_ID, occurrenceRequest);

        then(occurrence).should().setDate(DATE);
        then(occurrence).should().setTime(TIME);
        then(occurrence).should().setStatus(OccurrenceStatus.DONE);
        then(occurrence).should().setNote(NOTE);
        then(occurrence).should().setRemindMeBeforeDays(REMIND_ME_BEFORE_DAYS);
        then(occurrence).should().setReminded(true);
        then(occurrenceDao).should().save(occurrence);
        then(occurrenceRequestValidator).should().validate(occurrenceRequest);
    }

    @Test
    void editOccurrence_inheritValues() {
        given(occurrenceDao.findByIdValidated(OCCURRENCE_ID)).willReturn(occurrence);
        given(occurrence.getEventId()).willReturn(EVENT_ID);
        given(eventDao.findByIdValidated(EVENT_ID)).willReturn(event);

        given(occurrenceRequest.getDate()).willReturn(DATE);
        given(occurrenceRequest.getTime()).willReturn(TIME);
        given(occurrenceRequest.getStatus()).willReturn(OccurrenceStatus.DONE);
        given(occurrenceRequest.getNote()).willReturn(NOTE);
        given(occurrenceRequest.getRemindMeBeforeDays()).willReturn(REMIND_ME_BEFORE_DAYS);
        given(occurrenceRequest.getReminded()).willReturn(true);
        given(event.getTime()).willReturn(TIME);
        given(event.getRemindMeBeforeDays()).willReturn(REMIND_ME_BEFORE_DAYS);

        underTest.editOccurrence(OCCURRENCE_ID, occurrenceRequest);

        then(occurrence).should().setDate(DATE);
        then(occurrence).should().setTime(null);
        then(occurrence).should().setStatus(OccurrenceStatus.DONE);
        then(occurrence).should().setNote(NOTE);
        then(occurrence).should().setRemindMeBeforeDays(null);
        then(occurrence).should().setReminded(true);
        then(occurrenceDao).should().save(occurrence);
        then(occurrenceRequestValidator).should().validate(occurrenceRequest);
    }

    @Test
    void editOccurrenceStatus_nullStatus(){
        ExceptionValidator.validateInvalidParam(() -> underTest.editOccurrenceStatus(OCCURRENCE_ID, null), "status", "must not be null");
    }

    @Test
    void editOccurrenceStatus(){
        given(occurrenceDao.findByIdValidated(OCCURRENCE_ID)).willReturn(occurrence);
        given(occurrenceMapper.toResponse(occurrence)).willReturn(occurrenceResponse);

        assertThat(underTest.editOccurrenceStatus(OCCURRENCE_ID, OccurrenceStatus.DONE)).isEqualTo(occurrenceResponse);

        then(occurrence).should().setStatus(OccurrenceStatus.DONE);
        then(occurrenceDao).should().save(occurrence);
    }

    @Test
    void setReminded(){
        given(occurrenceDao.findByIdValidated(OCCURRENCE_ID)).willReturn(occurrence);
        given(occurrenceMapper.toResponse(occurrence)).willReturn(occurrenceResponse);

        assertThat(underTest.setReminded(OCCURRENCE_ID)).isEqualTo(occurrenceResponse);

        then(occurrence).should().setReminded(true);
        then(occurrenceDao).should().save(occurrence);
    }
}