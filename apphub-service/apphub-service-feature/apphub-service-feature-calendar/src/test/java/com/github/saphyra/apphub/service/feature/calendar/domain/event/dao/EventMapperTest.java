package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_ARCHIVED;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_AUTO_DONE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_CONTENT;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_END_DATE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_EXPIRATION_NOTIFIED;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REMIND_ME_BEFORE_DAYS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REPEAT_FOR_DAYS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REPETITION_DATA;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REPETITION_TYPE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_START_DATE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_TIME;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_TITLE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_EVENT;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EventMapperTest {
    private static final String USER_ID = "user-id";
    private static final String EVENT_ID = "event-id";
    private static final String REPETITION_TYPE = "DAYS_OF_WEEK";
    private static final String REPETITION_DATA = "{\"days\":[\"MONDAY\"]}";
    private static final String REPEAT_FOR_DAYS = "10";
    private static final String START_DATE = "2026-06-03";
    private static final String END_DATE = "2026-07-03";
    private static final String TIME = "14:30:00";
    private static final String TITLE = "Test Event";
    private static final String CONTENT = "Event Content";
    private static final String REMIND_ME_BEFORE_DAYS = "2";
    private static final String EXPIRATION_NOTIFIED = "false";
    private static final String ARCHIVED = "false";
    private static final String AUTO_DONE = "auto-done";

    @InjectMocks
    private EventMapper underTest;

    @Test
    public void testConvertDomain() {
        EventEntity domain = EventEntity.builder()
            .userId(USER_ID)
            .eventId(EVENT_ID)
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

        Map<String, AttributeValue> result = underTest.convertDomain(domain);

        assertThat(result.get(COLUMN_PK).s()).isEqualTo(PREFIX_USER + USER_ID);
        assertThat(result.get(COLUMN_SK).s()).isEqualTo(PREFIX_EVENT + EVENT_ID);
        assertThat(result.get(COLUMN_REPETITION_TYPE).s()).isEqualTo(REPETITION_TYPE);
        assertThat(result.get(COLUMN_REPETITION_DATA).s()).isEqualTo(REPETITION_DATA);
        assertThat(result.get(COLUMN_REPEAT_FOR_DAYS).s()).isEqualTo(REPEAT_FOR_DAYS);
        assertThat(result.get(COLUMN_START_DATE).s()).isEqualTo(START_DATE);
        assertThat(result.get(COLUMN_END_DATE).s()).isEqualTo(END_DATE);
        assertThat(result.get(COLUMN_TIME).s()).isEqualTo(TIME);
        assertThat(result.get(COLUMN_TITLE).s()).isEqualTo(TITLE);
        assertThat(result.get(COLUMN_CONTENT).s()).isEqualTo(CONTENT);
        assertThat(result.get(COLUMN_REMIND_ME_BEFORE_DAYS).s()).isEqualTo(REMIND_ME_BEFORE_DAYS);
        assertThat(result.get(COLUMN_EXPIRATION_NOTIFIED).s()).isEqualTo(EXPIRATION_NOTIFIED);
        assertThat(result.get(COLUMN_ARCHIVED).s()).isEqualTo(ARCHIVED);
        assertThat(result.get(COLUMN_AUTO_DONE).s()).isEqualTo(AUTO_DONE);
    }

    @Test
    public void testConvertEntity() {
        Map<String, AttributeValue> entity = Map.ofEntries(
            Map.entry(COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + USER_ID).build()),
            Map.entry(COLUMN_SK, AttributeValue.builder().s(PREFIX_EVENT + EVENT_ID).build()),
            Map.entry(COLUMN_REPETITION_TYPE, AttributeValue.builder().s(REPETITION_TYPE).build()),
            Map.entry(COLUMN_REPETITION_DATA, AttributeValue.builder().s(REPETITION_DATA).build()),
            Map.entry(COLUMN_REPEAT_FOR_DAYS, AttributeValue.builder().s(REPEAT_FOR_DAYS).build()),
            Map.entry(COLUMN_START_DATE, AttributeValue.builder().s(START_DATE).build()),
            Map.entry(COLUMN_END_DATE, AttributeValue.builder().s(END_DATE).build()),
            Map.entry(COLUMN_TIME, AttributeValue.builder().s(TIME).build()),
            Map.entry(COLUMN_TITLE, AttributeValue.builder().s(TITLE).build()),
            Map.entry(COLUMN_CONTENT, AttributeValue.builder().s(CONTENT).build()),
            Map.entry(COLUMN_REMIND_ME_BEFORE_DAYS, AttributeValue.builder().s(REMIND_ME_BEFORE_DAYS).build()),
            Map.entry(COLUMN_EXPIRATION_NOTIFIED, AttributeValue.builder().s(EXPIRATION_NOTIFIED).build()),
            Map.entry(COLUMN_ARCHIVED, AttributeValue.builder().s(ARCHIVED).build()),
            Map.entry(COLUMN_AUTO_DONE, AttributeValue.builder().s(AUTO_DONE).build())
        );

        EventEntity result = underTest.convertEntity(entity);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getEventId()).isEqualTo(EVENT_ID);
        assertThat(result.getRepetitionType()).isEqualTo(REPETITION_TYPE);
        assertThat(result.getRepetitionData()).isEqualTo(REPETITION_DATA);
        assertThat(result.getRepeatForDays()).isEqualTo(REPEAT_FOR_DAYS);
        assertThat(result.getStartDate()).isEqualTo(START_DATE);
        assertThat(result.getEndDate()).isEqualTo(END_DATE);
        assertThat(result.getTime()).isEqualTo(TIME);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getContent()).isEqualTo(CONTENT);
        assertThat(result.getRemindMeBeforeDays()).isEqualTo(REMIND_ME_BEFORE_DAYS);
        assertThat(result.getExpirationNotified()).isEqualTo(EXPIRATION_NOTIFIED);
        assertThat(result.getArchived()).isEqualTo(ARCHIVED);
        assertThat(result.getAutoDone()).isEqualTo(AUTO_DONE);
    }
}