package com.github.saphyra.apphub.service.feature.calendar.domain;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CommonCalendarDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao.DeprecatedEventLabelMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao.DeprecatedEventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.deprecated_dao.DeprecatedLabel;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.deprecated_dao.DeprecatedLabelDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrenceDao;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CalendarMigrationServiceTest {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final String LABEL_TEXT = "Test Label";
    private static final String EVENT_TITLE = "Test Event";
    private static final String EVENT_CONTENT = "Test Content";
    private static final LocalDate START_DATE = LocalDate.of(2026, 6, 3);
    private static final LocalDate END_DATE = LocalDate.of(2026, 6, 10);
    private static final LocalTime EVENT_TIME = LocalTime.of(10, 30);
    private static final LocalDate OCCURRENCE_DATE = LocalDate.of(2026, 6, 4);
    private static final LocalTime OCCURRENCE_TIME = LocalTime.of(11, 0);
    private static final Integer REMIND_ME_BEFORE_DAYS = 2;
    private static final String REPETITION_DATA = "test_data";
    private static final Integer REPEAT_FOR_DAYS = 10;
    private static final OccurrenceStatus OCCURRENCE_STATUS = OccurrenceStatus.PENDING;
    private static final String OCCURRENCE_NOTE = "Test note";
    private static final boolean REMINDED = false;
    private static final UUID USER_ID_2 = UUID.randomUUID();

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private DeprecatedLabelDao deprecatedLabelDao;

    @Mock
    private LabelDao labelDao;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private DeprecatedEventDao deprecatedEventDao;

    @Mock
    private DeprecatedOccurrenceDao deprecatedOccurrenceDao;

    @Mock
    private DeprecatedEventLabelMappingDao deprecatedEventLabelMappingDao;

    @Mock
    private CommonCalendarDao commonCalendarDao;

    @InjectMocks
    private CalendarMigrationService underTest;

    @Test
    void migrate() throws Exception {
        given(jdbcTemplate.query(eq("SELECT user_id FROM calendar.label GROUP BY user_id"), any(RowMapper.class))).willReturn(List.of(USER_ID_1));
        given(accessTokenProvider.set(AccessToken.builder().userId(USER_ID_1).build())).willReturn(accessTokenProvider);
        DeprecatedLabel deprecatedLabel = DeprecatedLabel.builder()
            .labelId(LABEL_ID)
            .label(LABEL_TEXT)
            .build();
        given(deprecatedLabelDao.getByUserId(USER_ID_1)).willReturn(List.of(deprecatedLabel));

        given(jdbcTemplate.query(eq("SELECT user_id FROM calendar.event GROUP BY user_id"), any(RowMapper.class))).willReturn(List.of(USER_ID_2));
        given(accessTokenProvider.set(AccessToken.builder().userId(USER_ID_2).build())).willReturn(accessTokenProvider);
        DeprecatedEvent deprecatedEvent = DeprecatedEvent.builder()
            .eventId(EVENT_ID)
            .userId(USER_ID_2)
            .repetitionType(RepetitionType.EVERY_X_DAYS)
            .repetitionData(REPETITION_DATA)
            .repeatForDays(REPEAT_FOR_DAYS)
            .startDate(START_DATE)
            .endDate(END_DATE)
            .time(EVENT_TIME)
            .title(EVENT_TITLE)
            .content(EVENT_CONTENT)
            .remindMeBeforeDays(REMIND_ME_BEFORE_DAYS)
            .expirationNotified(false)
            .archived(false)
            .build();
        given(deprecatedEventDao.getByUserId(USER_ID_2)).willReturn(List.of(deprecatedEvent));
        DeprecatedOccurrence deprecatedOccurrence = DeprecatedOccurrence.builder()
            .userId(USER_ID_2)
            .occurrenceId(OCCURRENCE_ID)
            .eventId(EVENT_ID)
            .date(OCCURRENCE_DATE)
            .time(OCCURRENCE_TIME)
            .status(OCCURRENCE_STATUS)
            .note(OCCURRENCE_NOTE)
            .remindMeBeforeDays(REMIND_ME_BEFORE_DAYS)
            .reminded(REMINDED)
            .build();
        given(deprecatedOccurrenceDao.getByEventId(EVENT_ID)).willReturn(List.of(deprecatedOccurrence));
        DeprecatedEventLabelMapping deprecatedEventLabelMapping = DeprecatedEventLabelMapping.builder()
            .userId(USER_ID_2)
            .labelId(LABEL_ID)
            .eventId(EVENT_ID)
            .build();
        given(deprecatedEventLabelMappingDao.getByEventId(EVENT_ID)).willReturn(List.of(deprecatedEventLabelMapping));

        underTest.migrate();

        then(accessTokenProvider).should(times(2)).close();
        then(labelDao).should().save(USER_ID_1, Label.builder().labelId(LABEL_ID).label(LABEL_TEXT).build());
        then(deprecatedLabelDao).should().delete(deprecatedLabel);
        then(deprecatedEventDao).should().delete(deprecatedEvent);

        ArgumentCaptor<Event> eventArgumentCaptor = ArgumentCaptor.forClass(Event.class);
        ArgumentCaptor<List<Occurrence>> occurrenceArgumentCaptor = ArgumentCaptor.forClass(List.class);
        then(commonCalendarDao).should().saveNewEvent(eventArgumentCaptor.capture(), occurrenceArgumentCaptor.capture(), eq(List.of(LABEL_ID)));

        assertThat(eventArgumentCaptor.getValue())
            .returns(EVENT_ID, Event::getEventId)
            .returns(USER_ID_2, Event::getUserId)
            .returns(RepetitionType.EVERY_X_DAYS, Event::getRepetitionType)
            .returns(REPETITION_DATA, Event::getRepetitionData)
            .returns(REPEAT_FOR_DAYS, Event::getRepeatForDays)
            .returns(START_DATE, Event::getStartDate)
            .returns(END_DATE, Event::getEndDate)
            .returns(EVENT_TIME, Event::getTime)
            .returns(EVENT_TITLE, Event::getTitle)
            .returns(EVENT_CONTENT, Event::getContent)
            .returns(REMIND_ME_BEFORE_DAYS, Event::getRemindMeBeforeDays)
            .returns(false, Event::isExpirationNotified)
            .returns(false, Event::isArchived);

        CustomAssertions.singleListAssertThat(occurrenceArgumentCaptor.getValue())
            .returns(USER_ID_2, Occurrence::getUserId)
            .returns(EVENT_ID, Occurrence::getEventId)
            .returns(OCCURRENCE_ID, Occurrence::getOccurrenceId)
            .returns(OCCURRENCE_DATE, Occurrence::getDate)
            .returns(OCCURRENCE_TIME, Occurrence::getTime)
            .returns(OCCURRENCE_STATUS, Occurrence::getStatus)
            .returns(OCCURRENCE_NOTE, Occurrence::getNote)
            .returns(REMIND_ME_BEFORE_DAYS, Occurrence::getRemindMeBeforeDays)
            .returns(REMINDED, Occurrence::isReminded);
    }
}


