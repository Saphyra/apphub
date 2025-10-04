package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OccurrenceQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final LocalDate START_DATE = LocalDate.now().minusDays(3);
    private static final LocalDate END_DATE = LocalDate.now().plusDays(3);
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private EventLabelMappingDao eventLabelMappingDao;

    @Mock
    private OccurrenceMapper occurrenceMapper;

    @Mock
    private EventDao eventDao;

    @InjectMocks
    private OccurrenceQueryService underTest;

    @Mock
    private OccurrenceResponse occurrenceResponse1;

    @Mock
    private OccurrenceResponse occurrenceResponse2;

    @Mock
    private EventLabelMapping eventLabelMapping;

    @Mock
    private Occurrence occurrence1;

    @Mock
    private Occurrence occurrence2;

    @Mock
    private Occurrence.OccurrenceBuilder occurrenceBuilder;

    @Mock
    private Event event;

    @Test
    void getOccurrences_eventNotInLabelFilter() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(eventLabelMappingDao.getByUserIdAndLabelId(USER_ID, LABEL_ID)).willReturn(List.of(eventLabelMapping));
        given(eventLabelMapping.getEventId()).willReturn(EVENT_ID);
        given(occurrence1.getEventId()).willReturn(UUID.randomUUID());
        given(occurrenceDao.getByUserId(USER_ID)).willReturn(List.of(occurrence1));

        assertThat(underTest.getOccurrences(USER_ID, START_DATE, END_DATE, LABEL_ID)).isEmpty();
    }

    @Test
    void getOccurrences_singlePendingOccurrence() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(eventLabelMappingDao.getByUserIdAndLabelId(USER_ID, LABEL_ID)).willReturn(List.of(eventLabelMapping));
        given(eventLabelMapping.getEventId()).willReturn(EVENT_ID);
        given(occurrence1.getEventId()).willReturn(EVENT_ID);
        given(occurrenceDao.getByUserId(USER_ID)).willReturn(List.of(occurrence1));
        given(occurrence1.getDate()).willReturn(CURRENT_DATE.plusDays(1));
        given(occurrenceMapper.toResponse(any(), eq(occurrence1))).willReturn(occurrenceResponse1);
        given(eventDao.findByIdValidated(EVENT_ID)).willReturn(event);
        given(occurrence1.getRemindMeBeforeDays()).willReturn(null);
        given(event.getRemindMeBeforeDays()).willReturn(null);

        assertThat(underTest.getOccurrences(USER_ID, START_DATE, END_DATE, LABEL_ID)).containsExactly(occurrenceResponse1);
    }

    @Test
    void getOccurrences_dontAddExpiredOccurrenceIfCurrentDateNotInRange() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(eventLabelMappingDao.getByUserIdAndLabelId(USER_ID, LABEL_ID)).willReturn(List.of(eventLabelMapping));
        given(eventLabelMapping.getEventId()).willReturn(EVENT_ID);
        given(occurrence1.getEventId()).willReturn(EVENT_ID);
        given(occurrenceDao.getByUserId(USER_ID)).willReturn(List.of(occurrence1));
        given(occurrence1.getDate()).willReturn(CURRENT_DATE.minusDays(5));
        given(occurrenceMapper.toResponse(any(), eq(occurrence1))).willReturn(occurrenceResponse1);
        given(eventDao.findByIdValidated(EVENT_ID)).willReturn(event);
        given(occurrence1.getRemindMeBeforeDays()).willReturn(null);
        given(event.getRemindMeBeforeDays()).willReturn(null);
        given(occurrence1.getStatus()).willReturn(OccurrenceStatus.EXPIRED);

        assertThat(underTest.getOccurrences(USER_ID, CURRENT_DATE.minusDays(10), CURRENT_DATE.minusDays(3), LABEL_ID)).containsExactly(occurrenceResponse1);
    }

    @Test
    void getOccurrences_addExpiredOccurrence() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(eventLabelMappingDao.getByUserIdAndLabelId(USER_ID, LABEL_ID)).willReturn(List.of(eventLabelMapping));
        given(eventLabelMapping.getEventId()).willReturn(EVENT_ID);
        given(occurrence1.getEventId()).willReturn(EVENT_ID);
        given(occurrenceDao.getByUserId(USER_ID)).willReturn(List.of(occurrence1));
        given(occurrence1.getDate()).willReturn(CURRENT_DATE.plusDays(1));
        given(occurrenceMapper.toResponse(any(), eq(occurrence1))).willReturn(occurrenceResponse1);
        given(occurrenceMapper.toResponse(any(), eq(occurrence2))).willReturn(occurrenceResponse2);
        given(eventDao.findByIdValidated(EVENT_ID)).willReturn(event);
        given(occurrence1.getRemindMeBeforeDays()).willReturn(null);
        given(event.getRemindMeBeforeDays()).willReturn(null);
        given(occurrence1.getStatus()).willReturn(OccurrenceStatus.EXPIRED);
        given(occurrence1.toBuilder()).willReturn(occurrenceBuilder);
        given(occurrenceBuilder.date(CURRENT_DATE)).willReturn(occurrenceBuilder);
        given(occurrenceBuilder.build()).willReturn(occurrence2);

        assertThat(underTest.getOccurrences(USER_ID, START_DATE, END_DATE, LABEL_ID)).containsExactlyInAnyOrder(occurrenceResponse1, occurrenceResponse2);
    }

    @Test
    void getOccurrences_addReminderForCurrentDay() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(eventLabelMappingDao.getByUserIdAndLabelId(USER_ID, LABEL_ID)).willReturn(List.of(eventLabelMapping));
        given(eventLabelMapping.getEventId()).willReturn(EVENT_ID);
        given(occurrence1.getEventId()).willReturn(EVENT_ID);
        given(occurrenceDao.getByUserId(USER_ID)).willReturn(List.of(occurrence1));
        given(occurrence1.getDate()).willReturn(CURRENT_DATE.plusDays(1));
        given(occurrenceMapper.toResponse(any(), eq(occurrence1))).willReturn(occurrenceResponse1);
        given(eventDao.findByIdValidated(EVENT_ID)).willReturn(event);
        given(occurrence1.getRemindMeBeforeDays()).willReturn(null);
        given(event.getRemindMeBeforeDays()).willReturn(5);
        given(occurrence1.toBuilder()).willReturn(occurrenceBuilder);
        given(occurrenceBuilder.date(CURRENT_DATE)).willReturn(occurrenceBuilder);
        given(occurrenceBuilder.status(OccurrenceStatus.REMINDER)).willReturn(occurrenceBuilder);
        given(occurrenceBuilder.build()).willReturn(occurrence2);
        given(occurrenceMapper.toResponse(any(), eq(occurrence2))).willReturn(occurrenceResponse2);

        assertThat(underTest.getOccurrences(USER_ID, START_DATE, END_DATE, LABEL_ID)).containsExactlyInAnyOrder(occurrenceResponse1, occurrenceResponse2);
    }

    @Test
    void getOccurrences_addReminderForCorrectDay() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(eventLabelMappingDao.getByUserIdAndLabelId(USER_ID, LABEL_ID)).willReturn(List.of(eventLabelMapping));
        given(eventLabelMapping.getEventId()).willReturn(EVENT_ID);
        given(occurrence1.getEventId()).willReturn(EVENT_ID);
        given(occurrenceDao.getByUserId(USER_ID)).willReturn(List.of(occurrence1));
        given(occurrence1.getDate()).willReturn(CURRENT_DATE.plusDays(2));
        given(occurrenceMapper.toResponse(any(), eq(occurrence1))).willReturn(occurrenceResponse1);
        given(eventDao.findByIdValidated(EVENT_ID)).willReturn(event);
        given(occurrence1.getRemindMeBeforeDays()).willReturn(null);
        given(event.getRemindMeBeforeDays()).willReturn(1);
        given(occurrence1.toBuilder()).willReturn(occurrenceBuilder);
        given(occurrenceBuilder.date(CURRENT_DATE.plusDays(1))).willReturn(occurrenceBuilder);
        given(occurrenceBuilder.status(OccurrenceStatus.REMINDER)).willReturn(occurrenceBuilder);
        given(occurrenceBuilder.build()).willReturn(occurrence2);
        given(occurrenceMapper.toResponse(any(), eq(occurrence2))).willReturn(occurrenceResponse2);

        assertThat(underTest.getOccurrences(USER_ID, START_DATE, END_DATE, LABEL_ID)).containsExactlyInAnyOrder(occurrenceResponse1, occurrenceResponse2);
    }

    @Test
    void getOccurrences_reminded() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(eventLabelMappingDao.getByUserIdAndLabelId(USER_ID, LABEL_ID)).willReturn(List.of(eventLabelMapping));
        given(eventLabelMapping.getEventId()).willReturn(EVENT_ID);
        given(occurrence1.getEventId()).willReturn(EVENT_ID);
        given(occurrenceDao.getByUserId(USER_ID)).willReturn(List.of(occurrence1));
        given(occurrence1.getDate()).willReturn(CURRENT_DATE.plusDays(2));
        given(occurrenceMapper.toResponse(any(), eq(occurrence1))).willReturn(occurrenceResponse1);
        given(eventDao.findByIdValidated(EVENT_ID)).willReturn(event);
        given(occurrence1.getRemindMeBeforeDays()).willReturn(null);
        given(event.getRemindMeBeforeDays()).willReturn(1);
        given(occurrence1.getReminded()).willReturn(true);

        assertThat(underTest.getOccurrences(USER_ID, START_DATE, END_DATE, LABEL_ID)).containsExactlyInAnyOrder(occurrenceResponse1);
    }

    @Test
    void getOccurrencesOfEvent() {
        given(occurrenceDao.getByEventId(EVENT_ID)).willReturn(List.of(occurrence1));
        given(occurrenceMapper.toResponse(any(), eq(occurrence1))).willReturn(occurrenceResponse1);

        assertThat(underTest.getOccurrencesOfEvent(EVENT_ID)).containsExactly(occurrenceResponse1);
    }

    @Test
    void getOccurrence() {
        given(occurrenceDao.findByIdValidated(OCCURRENCE_ID)).willReturn(occurrence1);
        given(occurrenceMapper.toResponse(occurrence1)).willReturn(occurrenceResponse1);

        assertThat(underTest.getOccurrence(OCCURRENCE_ID)).isEqualTo(occurrenceResponse1);
    }
}