package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
class EventConverterTest {
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EVENT_ID_STRING = "event-id";
    private static final String USER_ID_STRING = "user-id";
    private static final RepetitionType REPETITION_TYPE = RepetitionType.EVERY_X_DAYS;
    private static final String REPETITION_DATA = "repetition-data";
    private static final Integer REPEAT_FOR_DAYS = 7;
    private static final String REPEAT_FOR_DAYS_STRING = "7";
    private static final LocalDate START_DATE = LocalDate.of(2026, 6, 3);
    private static final String START_DATE_STRING = "2026-06-03";
    private static final LocalDate END_DATE = LocalDate.of(2026, 12, 31);
    private static final String END_DATE_STRING = "2026-12-31";
    private static final LocalTime TIME = LocalTime.of(14, 30, 0);
    private static final String TIME_STRING = "14:30";
    private static final String TITLE = "Event Title";
    private static final String CONTENT = "Event Content";
    private static final Integer REMIND_ME_BEFORE_DAYS = 2;
    private static final String REMIND_ME_BEFORE_DAYS_STRING = "2";
    private static final boolean EXPIRATION_NOTIFIED = true;
    private static final String EXPIRATION_NOTIFIED_STRING = "true";
    private static final boolean ARCHIVED = false;
    private static final String ARCHIVED_STRING = "false";
    private static final boolean AUTO_DONE = true;
    private static final String AUTO_DONE_STRING = "true";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DateTimeConverter dateTimeConverter;

    @InjectMocks
    private EventConverter underTest;

    @Test
    void convertDomain() {
        Event domain = Event.builder()
            .eventId(EVENT_ID)
            .userId(USER_ID)
            .repetitionType(REPETITION_TYPE)
            .repetitionData(REPETITION_DATA)
            .repeatForDays(REPEAT_FOR_DAYS)
            .startDate(START_DATE)
            .endDate(END_DATE)
            .time(TIME)
            .title(TITLE)
            .content(CONTENT)
            .remindMeBeforeDays(REMIND_ME_BEFORE_DAYS)
            .expirationNotified(EXPIRATION_NOTIFIED)
            .archived(ARCHIVED)
            .autoDone(AUTO_DONE)
            .build();

        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(dateTimeConverter.convertDomain(START_DATE)).willReturn(START_DATE_STRING);
        given(dateTimeConverter.convertDomain(END_DATE)).willReturn(END_DATE_STRING);
        given(dateTimeConverter.convertDomain(TIME)).willReturn(TIME_STRING);

        EventEntity result = underTest.convertDomain(domain);

        assertThat(result)
            .returns(EVENT_ID_STRING, EventEntity::getEventId)
            .returns(USER_ID_STRING, EventEntity::getUserId)
            .returns(REPETITION_TYPE.name(), EventEntity::getRepetitionType)
            .returns(REPETITION_DATA, EventEntity::getRepetitionData)
            .returns(REPEAT_FOR_DAYS_STRING, EventEntity::getRepeatForDays)
            .returns(START_DATE_STRING, EventEntity::getStartDate)
            .returns(END_DATE_STRING, EventEntity::getEndDate)
            .returns(TIME_STRING, EventEntity::getTime)
            .returns(TITLE, EventEntity::getTitle)
            .returns(CONTENT, EventEntity::getContent)
            .returns(REMIND_ME_BEFORE_DAYS_STRING, EventEntity::getRemindMeBeforeDays)
            .returns(EXPIRATION_NOTIFIED_STRING, EventEntity::getExpirationNotified)
            .returns(ARCHIVED_STRING, EventEntity::getArchived)
            .returns(AUTO_DONE_STRING, EventEntity::getAutoDone);
    }

    @Test
    void convertEntity() {
        EventEntity entity = EventEntity.builder()
            .eventId(EVENT_ID_STRING)
            .userId(USER_ID_STRING)
            .repetitionType(REPETITION_TYPE.name())
            .repetitionData(REPETITION_DATA)
            .repeatForDays(REPEAT_FOR_DAYS_STRING)
            .startDate(START_DATE_STRING)
            .endDate(END_DATE_STRING)
            .time(TIME_STRING)
            .title(TITLE)
            .content(CONTENT)
            .remindMeBeforeDays(REMIND_ME_BEFORE_DAYS_STRING)
            .expirationNotified(EXPIRATION_NOTIFIED_STRING)
            .archived(ARCHIVED_STRING)
            .autoDone(AUTO_DONE_STRING)
            .build();

        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(dateTimeConverter.convertToLocalDate(START_DATE_STRING)).willReturn(START_DATE);
        given(dateTimeConverter.convertToLocalDate(END_DATE_STRING)).willReturn(END_DATE);
        given(dateTimeConverter.convertToLocalTime(TIME_STRING)).willReturn(TIME);

        Event result = underTest.convertEntity(entity);

        assertThat(result)
            .returns(EVENT_ID, Event::getEventId)
            .returns(USER_ID, Event::getUserId)
            .returns(REPETITION_TYPE, Event::getRepetitionType)
            .returns(REPETITION_DATA, Event::getRepetitionData)
            .returns(REPEAT_FOR_DAYS, Event::getRepeatForDays)
            .returns(START_DATE, Event::getStartDate)
            .returns(END_DATE, Event::getEndDate)
            .returns(TIME, Event::getTime)
            .returns(TITLE, Event::getTitle)
            .returns(CONTENT, Event::getContent)
            .returns(REMIND_ME_BEFORE_DAYS, Event::getRemindMeBeforeDays)
            .returns(EXPIRATION_NOTIFIED, Event::isExpirationNotified)
            .returns(ARCHIVED, Event::isArchived)
            .returns(AUTO_DONE, Event::isAutoDone);
    }
}