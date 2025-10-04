package com.github.saphyra.apphub.service.calendar.migration;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static com.github.saphyra.apphub.service.calendar.migration.MigrationDao.COLUMN_CONTENT;
import static com.github.saphyra.apphub.service.calendar.migration.MigrationDao.COLUMN_DATE;
import static com.github.saphyra.apphub.service.calendar.migration.MigrationDao.COLUMN_EVENT_ID;
import static com.github.saphyra.apphub.service.calendar.migration.MigrationDao.COLUMN_NOTE;
import static com.github.saphyra.apphub.service.calendar.migration.MigrationDao.COLUMN_OCCURRENCE_ID;
import static com.github.saphyra.apphub.service.calendar.migration.MigrationDao.COLUMN_REPEAT;
import static com.github.saphyra.apphub.service.calendar.migration.MigrationDao.COLUMN_REPETITION_DATA;
import static com.github.saphyra.apphub.service.calendar.migration.MigrationDao.COLUMN_REPETITION_TYPE;
import static com.github.saphyra.apphub.service.calendar.migration.MigrationDao.COLUMN_START_DATE;
import static com.github.saphyra.apphub.service.calendar.migration.MigrationDao.COLUMN_STATUS;
import static com.github.saphyra.apphub.service.calendar.migration.MigrationDao.COLUMN_TIME;
import static com.github.saphyra.apphub.service.calendar.migration.MigrationDao.COLUMN_TITLE;
import static com.github.saphyra.apphub.service.calendar.migration.MigrationDao.COLUMN_USER_ID;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MigrationDaoTest {
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ENCRYPTED_START_DATE = "encrypted-start-date";
    private static final String ENCRYPTED_TIME = "encrypted-time";
    private static final String ENCRYPTED_REPETITION_DATA = "encrypted-repetition-data";
    private static final String ENCRYPTED_TITLE = "encrypted-title";
    private static final String ENCRYPTED_CONTENT = "encrypted-content";
    private static final LocalDate START_DATE = LocalDate.now();
    private static final LocalTime TIME = LocalTime.now();
    private static final String REPETITION_DATA = "repetition-data";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final LocalDate DATE = LocalDate.now().plusDays(1);
    private static final String ENCRYPTED_DATE = "encrypted-date";
    private static final String ENCRYPTED_NOTE = "encrypted-note";
    private static final String NOTE = "note";
    private static final String ENCRYPTED_REPEAT = "encrypted-repeat";
    private static final Integer REPEAT = 23;

    @MockBean
    private StringEncryptor stringEncryptor;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MigrationDao underTest;

    @AfterEach
    void clear() {
        jdbcTemplate.execute("DELETE FROM calendar.event_old");
        jdbcTemplate.execute("DELETE FROM calendar.occurrence_old");
    }

    @Test
    void getAllEvents() {
        String sql = "INSERT INTO calendar.event_old " +
            "(%s, %s, %s, %s, %s, %s, %s, %s, %s) ".formatted(COLUMN_EVENT_ID, COLUMN_USER_ID, COLUMN_START_DATE, COLUMN_TIME, COLUMN_REPETITION_TYPE, COLUMN_REPETITION_DATA, COLUMN_TITLE, COLUMN_CONTENT, COLUMN_REPEAT) +
            String.format(
                "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                EVENT_ID,
                USER_ID,
                ENCRYPTED_START_DATE,
                ENCRYPTED_TIME,
                RepetitionType.EVERY_X_DAYS,
                ENCRYPTED_REPETITION_DATA,
                ENCRYPTED_TITLE,
                ENCRYPTED_CONTENT,
                ENCRYPTED_REPEAT
            );

        given(stringEncryptor.decrypt(ENCRYPTED_START_DATE, USER_ID.toString(), EVENT_ID.toString(), "start-date")).willReturn(START_DATE.toString());
        given(stringEncryptor.decrypt(ENCRYPTED_TIME, USER_ID.toString(), EVENT_ID.toString(), COLUMN_TIME)).willReturn(TIME.toString());
        given(stringEncryptor.decrypt(ENCRYPTED_REPETITION_DATA, USER_ID.toString(), EVENT_ID.toString(), "repetition-data")).willReturn(REPETITION_DATA);
        given(stringEncryptor.decrypt(ENCRYPTED_TITLE, USER_ID.toString(), EVENT_ID.toString(), "title")).willReturn(TITLE);
        given(stringEncryptor.decrypt(ENCRYPTED_CONTENT, USER_ID.toString(), EVENT_ID.toString(), "content")).willReturn(CONTENT);
        given(stringEncryptor.decrypt(ENCRYPTED_REPEAT, USER_ID.toString(), EVENT_ID.toString(), COLUMN_REPEAT)).willReturn(REPEAT.toString());

        jdbcTemplate.update(sql);

        CustomAssertions.singleListAssertThat(underTest.getAllEvents())
            .returns(EVENT_ID, DeprecatedEvent::getEventId)
            .returns(USER_ID, DeprecatedEvent::getUserId)
            .returns(START_DATE, DeprecatedEvent::getStartDate)
            .returns(TIME, DeprecatedEvent::getTime)
            .returns(RepetitionType.EVERY_X_DAYS, DeprecatedEvent::getRepetitionType)
            .returns(REPETITION_DATA, DeprecatedEvent::getRepetitionData)
            .returns(TITLE, DeprecatedEvent::getTitle)
            .returns(CONTENT, DeprecatedEvent::getContent)
            .returns(REPEAT, DeprecatedEvent::getRepeat);
    }

    @Test
    void getAllEvents_invalidRepeat() {
        String sql = "INSERT INTO calendar.event_old " +
            "(%s, %s, %s, %s, %s, %s, %s, %s, %s) ".formatted(COLUMN_EVENT_ID, COLUMN_USER_ID, COLUMN_START_DATE, COLUMN_TIME, COLUMN_REPETITION_TYPE, COLUMN_REPETITION_DATA, COLUMN_TITLE, COLUMN_CONTENT, COLUMN_REPEAT) +
            String.format(
                "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                EVENT_ID,
                USER_ID,
                ENCRYPTED_START_DATE,
                ENCRYPTED_TIME,
                RepetitionType.EVERY_X_DAYS,
                ENCRYPTED_REPETITION_DATA,
                ENCRYPTED_TITLE,
                ENCRYPTED_CONTENT,
                ENCRYPTED_REPEAT
            );

        given(stringEncryptor.decrypt(ENCRYPTED_START_DATE, USER_ID.toString(), EVENT_ID.toString(), "start-date")).willReturn(START_DATE.toString());
        given(stringEncryptor.decrypt(ENCRYPTED_TIME, USER_ID.toString(), EVENT_ID.toString(), COLUMN_TIME)).willReturn(TIME.toString());
        given(stringEncryptor.decrypt(ENCRYPTED_REPETITION_DATA, USER_ID.toString(), EVENT_ID.toString(), "repetition-data")).willReturn(REPETITION_DATA);
        given(stringEncryptor.decrypt(ENCRYPTED_TITLE, USER_ID.toString(), EVENT_ID.toString(), "title")).willReturn(TITLE);
        given(stringEncryptor.decrypt(ENCRYPTED_CONTENT, USER_ID.toString(), EVENT_ID.toString(), "content")).willReturn(CONTENT);
        given(stringEncryptor.decrypt(ENCRYPTED_REPEAT, USER_ID.toString(), EVENT_ID.toString(), COLUMN_REPEAT)).willReturn("0");

        jdbcTemplate.update(sql);

        CustomAssertions.singleListAssertThat(underTest.getAllEvents())
            .returns(EVENT_ID, DeprecatedEvent::getEventId)
            .returns(USER_ID, DeprecatedEvent::getUserId)
            .returns(START_DATE, DeprecatedEvent::getStartDate)
            .returns(TIME, DeprecatedEvent::getTime)
            .returns(RepetitionType.EVERY_X_DAYS, DeprecatedEvent::getRepetitionType)
            .returns(REPETITION_DATA, DeprecatedEvent::getRepetitionData)
            .returns(TITLE, DeprecatedEvent::getTitle)
            .returns(CONTENT, DeprecatedEvent::getContent)
            .returns(1, DeprecatedEvent::getRepeat);
    }

    @Test
    void getAllEvents_nullRepeat() {
        String sql = "INSERT INTO calendar.event_old " +
            "(%s, %s, %s, %s, %s, %s, %s, %s, %s) ".formatted(COLUMN_EVENT_ID, COLUMN_USER_ID, COLUMN_START_DATE, COLUMN_TIME, COLUMN_REPETITION_TYPE, COLUMN_REPETITION_DATA, COLUMN_TITLE, COLUMN_CONTENT, COLUMN_REPEAT) +
            String.format(
                "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                EVENT_ID,
                USER_ID,
                ENCRYPTED_START_DATE,
                ENCRYPTED_TIME,
                RepetitionType.EVERY_X_DAYS,
                ENCRYPTED_REPETITION_DATA,
                ENCRYPTED_TITLE,
                ENCRYPTED_CONTENT,
                null
            );

        given(stringEncryptor.decrypt(ENCRYPTED_START_DATE, USER_ID.toString(), EVENT_ID.toString(), "start-date")).willReturn(START_DATE.toString());
        given(stringEncryptor.decrypt(ENCRYPTED_TIME, USER_ID.toString(), EVENT_ID.toString(), COLUMN_TIME)).willReturn(TIME.toString());
        given(stringEncryptor.decrypt(ENCRYPTED_REPETITION_DATA, USER_ID.toString(), EVENT_ID.toString(), "repetition-data")).willReturn(REPETITION_DATA);
        given(stringEncryptor.decrypt(ENCRYPTED_TITLE, USER_ID.toString(), EVENT_ID.toString(), "title")).willReturn(TITLE);
        given(stringEncryptor.decrypt(ENCRYPTED_CONTENT, USER_ID.toString(), EVENT_ID.toString(), "content")).willReturn(CONTENT);
        given(stringEncryptor.decrypt(null, USER_ID.toString(), EVENT_ID.toString(), COLUMN_REPEAT)).willReturn(null);

        jdbcTemplate.update(sql);

        CustomAssertions.singleListAssertThat(underTest.getAllEvents())
            .returns(EVENT_ID, DeprecatedEvent::getEventId)
            .returns(USER_ID, DeprecatedEvent::getUserId)
            .returns(START_DATE, DeprecatedEvent::getStartDate)
            .returns(TIME, DeprecatedEvent::getTime)
            .returns(RepetitionType.EVERY_X_DAYS, DeprecatedEvent::getRepetitionType)
            .returns(REPETITION_DATA, DeprecatedEvent::getRepetitionData)
            .returns(TITLE, DeprecatedEvent::getTitle)
            .returns(CONTENT, DeprecatedEvent::getContent)
            .returns(1, DeprecatedEvent::getRepeat);
    }

    @Test
    void getOccurrences() {
        jdbcTemplate.execute(createOccurrenceSql(OCCURRENCE_ID, EVENT_ID, OccurrenceStatus.DONE));
        jdbcTemplate.execute(createOccurrenceSql(UUID.randomUUID(), UUID.randomUUID(), OccurrenceStatus.DONE));

        given(stringEncryptor.decrypt(ENCRYPTED_DATE, USER_ID.toString(), OCCURRENCE_ID.toString(), COLUMN_DATE)).willReturn(DATE.toString());
        given(stringEncryptor.decrypt(ENCRYPTED_TIME, USER_ID.toString(), OCCURRENCE_ID.toString(), COLUMN_TIME)).willReturn(TIME.toString());
        given(stringEncryptor.decrypt(ENCRYPTED_NOTE, USER_ID.toString(), OCCURRENCE_ID.toString(), COLUMN_NOTE)).willReturn(NOTE);

        CustomAssertions.singleListAssertThat(underTest.getOccurrences(EVENT_ID))
            .returns(OCCURRENCE_ID, DeprecatedOccurrence::getOccurrenceId)
            .returns(EVENT_ID, DeprecatedOccurrence::getEventId)
            .returns(USER_ID, DeprecatedOccurrence::getUserId)
            .returns(DATE, DeprecatedOccurrence::getDate)
            .returns(TIME, DeprecatedOccurrence::getTime)
            .returns(OccurrenceStatus.DONE, DeprecatedOccurrence::getStatus)
            .returns("note", DeprecatedOccurrence::getNote);
    }

    @Test
    void getVirtualOccurrence() {
        jdbcTemplate.execute(createOccurrenceSql(OCCURRENCE_ID, EVENT_ID, "VIRTUAL"));

        given(stringEncryptor.decrypt(ENCRYPTED_DATE, USER_ID.toString(), OCCURRENCE_ID.toString(), COLUMN_DATE)).willReturn(DATE.toString());
        given(stringEncryptor.decrypt(ENCRYPTED_TIME, USER_ID.toString(), OCCURRENCE_ID.toString(), COLUMN_TIME)).willReturn(TIME.toString());
        given(stringEncryptor.decrypt(ENCRYPTED_NOTE, USER_ID.toString(), OCCURRENCE_ID.toString(), COLUMN_NOTE)).willReturn(NOTE);

        CustomAssertions.singleListAssertThat(underTest.getOccurrences(EVENT_ID))
            .returns(OCCURRENCE_ID, DeprecatedOccurrence::getOccurrenceId)
            .returns(EVENT_ID, DeprecatedOccurrence::getEventId)
            .returns(USER_ID, DeprecatedOccurrence::getUserId)
            .returns(DATE, DeprecatedOccurrence::getDate)
            .returns(TIME, DeprecatedOccurrence::getTime)
            .returns(OccurrenceStatus.PENDING, DeprecatedOccurrence::getStatus)
            .returns("note", DeprecatedOccurrence::getNote);
    }

    private String createOccurrenceSql(UUID occurrenceId, UUID eventId, Object status) {
        return "INSERT INTO calendar.occurrence_old (%s)".formatted(String.join(", ", COLUMN_OCCURRENCE_ID, COLUMN_EVENT_ID, COLUMN_USER_ID, COLUMN_DATE, COLUMN_TIME, COLUMN_STATUS, COLUMN_NOTE))
            + " VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s')".formatted(
            occurrenceId,
            eventId,
            USER_ID,
            ENCRYPTED_DATE,
            ENCRYPTED_TIME,
            status,
            ENCRYPTED_NOTE
        );
    }
}