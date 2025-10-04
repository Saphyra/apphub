package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
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

@ExtendWith(MockitoExtension.class)
class OccurrenceMapperTest {
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final LocalDate DATE = LocalDate.now();
    private static final LocalTime TIME = LocalTime.now();
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String NOTE = "note";
    private static final Integer REMIND_ME_BEFORE_DAYS = 3;

    @Mock
    private EventDao eventDao;

    @InjectMocks
    private OccurrenceMapper underTest;

    @Mock
    private Occurrence occurrence;

    @Mock
    private Event event;

    @Test
    void getValuesFromOccurrence(){
        given(occurrence.getOccurrenceId()).willReturn(OCCURRENCE_ID);
        given(occurrence.getEventId()).willReturn(EVENT_ID);
        given(occurrence.getDate()).willReturn(DATE);
        given(occurrence.getTime()).willReturn(TIME);
        given(occurrence.getStatus()).willReturn(OccurrenceStatus.DONE);
        given(occurrence.getNote()).willReturn(NOTE);
        given(occurrence.getRemindMeBeforeDays()).willReturn(REMIND_ME_BEFORE_DAYS);
        given(occurrence.getReminded()).willReturn(true);
        given(eventDao.findByIdValidated(EVENT_ID)).willReturn(event);
        given(event.getTitle()).willReturn(TITLE);
        given(event.getContent()).willReturn(CONTENT);

        assertThat(underTest.toResponse(occurrence))
            .returns(OCCURRENCE_ID, OccurrenceResponse::getOccurrenceId)
            .returns(EVENT_ID, OccurrenceResponse::getEventId)
            .returns(DATE, OccurrenceResponse::getDate)
            .returns(TIME, OccurrenceResponse::getTime)
            .returns(OccurrenceStatus.DONE, OccurrenceResponse::getStatus)
            .returns(TITLE, OccurrenceResponse::getTitle)
            .returns(CONTENT, OccurrenceResponse::getContent)
            .returns(NOTE, OccurrenceResponse::getNote)
            .returns(REMIND_ME_BEFORE_DAYS, OccurrenceResponse::getRemindMeBeforeDays)
            .returns(true, OccurrenceResponse::getReminded);
    }

    @Test
    void getValuesFromEvent(){
        given(occurrence.getOccurrenceId()).willReturn(OCCURRENCE_ID);
        given(occurrence.getEventId()).willReturn(EVENT_ID);
        given(occurrence.getDate()).willReturn(DATE);
        given(occurrence.getTime()).willReturn(null);
        given(occurrence.getStatus()).willReturn(OccurrenceStatus.DONE);
        given(occurrence.getNote()).willReturn(NOTE);
        given(occurrence.getRemindMeBeforeDays()).willReturn(null);
        given(occurrence.getReminded()).willReturn(true);
        given(eventDao.findByIdValidated(EVENT_ID)).willReturn(event);
        given(event.getTitle()).willReturn(TITLE);
        given(event.getContent()).willReturn(CONTENT);
        given(event.getTime()).willReturn(TIME);
        given(event.getRemindMeBeforeDays()).willReturn(REMIND_ME_BEFORE_DAYS);

        assertThat(underTest.toResponse(occurrence))
            .returns(OCCURRENCE_ID, OccurrenceResponse::getOccurrenceId)
            .returns(EVENT_ID, OccurrenceResponse::getEventId)
            .returns(DATE, OccurrenceResponse::getDate)
            .returns(TIME, OccurrenceResponse::getTime)
            .returns(OccurrenceStatus.DONE, OccurrenceResponse::getStatus)
            .returns(TITLE, OccurrenceResponse::getTitle)
            .returns(CONTENT, OccurrenceResponse::getContent)
            .returns(NOTE, OccurrenceResponse::getNote)
            .returns(REMIND_ME_BEFORE_DAYS, OccurrenceResponse::getRemindMeBeforeDays)
            .returns(true, OccurrenceResponse::getReminded);
    }
}