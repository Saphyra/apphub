package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContextFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ExpiredEventServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalDate EXTEND_UNTIL = CURRENT_DATE.plusWeeks(2);

    @Mock
    private EventDao eventDao;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private EventRequestValidator eventRequestValidator;

    @Mock
    private UpdateEventContextFactory updateEventContextFactory;

    @InjectMocks
    private ExpiredEventService underTest;

    @Mock
    private Event event;

    @Mock
    private Occurrence occurrence;

    @Mock
    private EventResponse eventResponse;

    @Mock
    private UpdateEventContext updateEventContext;

    @Test
    void getExpiredEvents_expirationNotified() {
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(event));
        given(event.isExpirationNotified()).willReturn(true);

        assertThat(underTest.getExpiredEvents(USER_ID)).isEmpty();
    }

    @Test
    void getExpiredEvents_oneTime() {
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(event));
        given(event.isExpirationNotified()).willReturn(false);
        given(event.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);

        assertThat(underTest.getExpiredEvents(USER_ID)).isEmpty();
    }

    @Test
    void getExpiredEvents_noOccurrence() {
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(event));
        given(event.isExpirationNotified()).willReturn(false);
        given(event.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);
        given(event.getEventId()).willReturn(EVENT_ID);
        given(event.getUserId()).willReturn(USER_ID);
        given(occurrenceDao.getByEventId(USER_ID, EVENT_ID)).willReturn(List.of());

        assertThat(underTest.getExpiredEvents(USER_ID)).isEmpty();
    }

    @Test
    void getExpiredEvents_notExpired() {
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(event));
        given(event.isExpirationNotified()).willReturn(false);
        given(event.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);
        given(event.getEventId()).willReturn(EVENT_ID);
        given(occurrenceDao.getByEventId(USER_ID, EVENT_ID)).willReturn(List.of(occurrence));
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(event.getUserId()).willReturn(USER_ID);
        given(occurrence.getDate()).willReturn(CURRENT_DATE.plusDays(1));

        assertThat(underTest.getExpiredEvents(USER_ID)).isEmpty();
    }

    @Test
    void getExpiredEvents_expired() {
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(event));
        given(event.isExpirationNotified()).willReturn(false);
        given(event.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);
        given(event.getEventId()).willReturn(EVENT_ID);
        given(occurrenceDao.getByEventId(USER_ID, EVENT_ID)).willReturn(List.of(occurrence));
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(occurrence.getDate()).willReturn(CURRENT_DATE);
        given(event.getUserId()).willReturn(USER_ID);
        given(eventMapper.toResponse(event)).willReturn(eventResponse);

        assertThat(underTest.getExpiredEvents(USER_ID)).containsExactly(eventResponse);
    }

    @Test
    void hide() {
        given(eventDao.findByIdValidated(USER_ID, EVENT_ID)).willReturn(event);

        underTest.hide(USER_ID, EVENT_ID);

        then(event).should().setExpirationNotified(true);
        then(eventDao).should().save(event);
    }

    @Test
    void extend_oneTimeEvent() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(eventDao.findByIdValidated(USER_ID, EVENT_ID)).willReturn(event);
        given(event.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);

        ExceptionValidator.validateInvalidParam(() -> underTest.extend(USER_ID, EVENT_ID, EXTEND_UNTIL), "eventId", "must not be one-time event");

        then(eventRequestValidator).should().validateDates(CURRENT_DATE, EXTEND_UNTIL);
    }

    @Test
    void extend() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(eventDao.findByIdValidated(USER_ID, EVENT_ID)).willReturn(event);
        given(event.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);
        given(updateEventContextFactory.create(event)).willReturn(updateEventContext);

        underTest.extend(USER_ID, EVENT_ID, EXTEND_UNTIL);

        then(eventRequestValidator).should().validateDates(CURRENT_DATE, EXTEND_UNTIL);
        then(event).should().setStartDate(CURRENT_DATE);
        then(event).should().setEndDate(EXTEND_UNTIL);
        then(updateEventContext).should().occurrenceRecreationNeeded();
        then(updateEventContext).should().processChanges();
    }
}