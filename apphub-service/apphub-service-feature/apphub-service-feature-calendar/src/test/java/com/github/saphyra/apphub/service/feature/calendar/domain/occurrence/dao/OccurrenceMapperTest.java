package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_AUTO_DONE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_DATE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_DATE_BUCKET;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_NOTE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REMINDED;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REMIND_ME_BEFORE_DAYS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_STATUS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_TIME;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_USER_ID;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_EVENT;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_OCCURRENCE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OccurrenceMapperTest {
    private static final String USER_ID = "user-id";
    private static final String EVENT_ID = "event-id";
    private static final String OCCURRENCE_ID = "occurrence-id";
    private static final String DATE_BUCKET = "2026-06";
    private static final String DATE = "2026-06-03";
    private static final String TIME = "14:30:00";
    private static final String STATUS = "PENDING";
    private static final String NOTE = "Occurrence note";
    private static final String REMIND_ME_BEFORE_DAYS = "2";
    private static final String REMINDED = "false";
    private static final String AUTO_DONE = "auto-done";

    @InjectMocks
    private OccurrenceMapper underTest;

    @Test
    void convertDomain() {
        OccurrenceEntity domain = OccurrenceEntity.builder()
            .userId(USER_ID)
            .eventId(EVENT_ID)
            .occurrenceId(OCCURRENCE_ID)
            .dateBucket(DATE_BUCKET)
            .date(DATE)
            .time(TIME)
            .status(STATUS)
            .note(NOTE)
            .remindMeBeforeDays(REMIND_ME_BEFORE_DAYS)
            .reminded(REMINDED)
            .autoDone(AUTO_DONE)
            .build();

        Map<String, AttributeValue> result = underTest.convertDomain(domain);

        assertThat(result.get(COLUMN_PK).s()).isEqualTo(PREFIX_EVENT + EVENT_ID);
        assertThat(result.get(COLUMN_SK).s()).isEqualTo(PREFIX_OCCURRENCE + OCCURRENCE_ID);
        assertThat(result.get(COLUMN_USER_ID).s()).isEqualTo(PREFIX_USER + USER_ID);
        assertThat(result.get(COLUMN_DATE_BUCKET).s()).isEqualTo(DATE_BUCKET);
        assertThat(result.get(COLUMN_DATE).s()).isEqualTo(DATE);
        assertThat(result.get(COLUMN_TIME).s()).isEqualTo(TIME);
        assertThat(result.get(COLUMN_STATUS).s()).isEqualTo(STATUS);
        assertThat(result.get(COLUMN_NOTE).s()).isEqualTo(NOTE);
        assertThat(result.get(COLUMN_REMIND_ME_BEFORE_DAYS).s()).isEqualTo(REMIND_ME_BEFORE_DAYS);
        assertThat(result.get(COLUMN_REMINDED).s()).isEqualTo(REMINDED);
        assertThat(result.get(COLUMN_AUTO_DONE).s()).isEqualTo(AUTO_DONE);
    }

    @Test
    void convertEntity() {
        Map<String, AttributeValue> entity = Map.ofEntries(
            Map.entry(COLUMN_PK, AttributeValue.builder().s(PREFIX_EVENT + EVENT_ID).build()),
            Map.entry(COLUMN_SK, AttributeValue.builder().s(PREFIX_OCCURRENCE + OCCURRENCE_ID).build()),
            Map.entry(COLUMN_USER_ID, AttributeValue.builder().s(PREFIX_USER + USER_ID).build()),
            Map.entry(COLUMN_DATE_BUCKET, AttributeValue.builder().s(DATE_BUCKET).build()),
            Map.entry(COLUMN_DATE, AttributeValue.builder().s(DATE).build()),
            Map.entry(COLUMN_TIME, AttributeValue.builder().s(TIME).build()),
            Map.entry(COLUMN_STATUS, AttributeValue.builder().s(STATUS).build()),
            Map.entry(COLUMN_NOTE, AttributeValue.builder().s(NOTE).build()),
            Map.entry(COLUMN_REMIND_ME_BEFORE_DAYS, AttributeValue.builder().s(REMIND_ME_BEFORE_DAYS).build()),
            Map.entry(COLUMN_REMINDED, AttributeValue.builder().s(REMINDED).build()),
            Map.entry(COLUMN_AUTO_DONE, AttributeValue.builder().s(AUTO_DONE).build())
        );

        OccurrenceEntity result = underTest.convertEntity(entity);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getEventId()).isEqualTo(EVENT_ID);
        assertThat(result.getOccurrenceId()).isEqualTo(OCCURRENCE_ID);
        assertThat(result.getDateBucket()).isEqualTo(DATE_BUCKET);
        assertThat(result.getDate()).isEqualTo(DATE);
        assertThat(result.getTime()).isEqualTo(TIME);
        assertThat(result.getStatus()).isEqualTo(STATUS);
        assertThat(result.getNote()).isEqualTo(NOTE);
        assertThat(result.getRemindMeBeforeDays()).isEqualTo(REMIND_ME_BEFORE_DAYS);
        assertThat(result.getReminded()).isEqualTo(REMINDED);
        assertThat(result.getAutoDone()).isEqualTo(AUTO_DONE);
    }
}