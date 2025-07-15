package com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.OccurrenceConverter.COLUMN_DATE;
import static com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.OccurrenceConverter.COLUMN_NOTE;
import static com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.OccurrenceConverter.COLUMN_TIME;
import static com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.OccurrenceConverter.COLUMN_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class OccurrenceConverterTest {
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDate DATE = LocalDate.now();
    private static final String NOTE = "note";
    private static final String OCCURRENCE_ID_STRING = "occurrence-id";
    private static final String EVENT_ID_STRING = "event-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String ENCRYPTED_DATE = "date";
    private static final String ENCRYPTED_NOTE = "encrypted-note";
    private static final LocalTime TIME = LocalTime.now();
    private static final String ENCRYPTED_TIME = "encrypted-time";
    private static final String ENCRYPTED_TYPE = "encrypted-type";
    private static final String ACCESS_TOKEN_USER_ID = "access-token-user-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private OccurrenceConverter underTest;

    @BeforeEach
    public void setUp() {
        given(accessTokenProvider.getUserIdAsString()).willReturn(ACCESS_TOKEN_USER_ID);
    }

    @Test
    public void convertDomain() {
        Occurrence occurrence = Occurrence.builder()
            .occurrenceId(OCCURRENCE_ID)
            .eventId(EVENT_ID)
            .userId(USER_ID)
            .date(DATE)
            .time(TIME)
            .status(OccurrenceStatus.SNOOZED)
            .note(NOTE)
            .type(OccurrenceType.FOLLOW_UP)
            .build();

        given(uuidConverter.convertDomain(OCCURRENCE_ID)).willReturn(OCCURRENCE_ID_STRING);
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(stringEncryptor.encrypt(DATE.toString(), ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_DATE)).willReturn(ENCRYPTED_DATE);
        given(stringEncryptor.encrypt(TIME.toString(), ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_TIME)).willReturn(ENCRYPTED_TIME);
        given(stringEncryptor.encrypt(NOTE, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_NOTE)).willReturn(ENCRYPTED_NOTE);
        given(stringEncryptor.encrypt(OccurrenceType.FOLLOW_UP.name(), ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_TYPE)).willReturn(ENCRYPTED_TYPE);

        OccurrenceEntity result = underTest.convertDomain(occurrence);

        assertThat(result.getOccurrenceId()).isEqualTo(OCCURRENCE_ID_STRING);
        assertThat(result.getEventId()).isEqualTo(EVENT_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getDate()).isEqualTo(ENCRYPTED_DATE);
        assertThat(result.getTime()).isEqualTo(ENCRYPTED_TIME);
        assertThat(result.getStatus()).isEqualTo(OccurrenceStatus.SNOOZED);
        assertThat(result.getNote()).isEqualTo(ENCRYPTED_NOTE);
        assertThat(result.getType()).isEqualTo(ENCRYPTED_TYPE);
    }

    @Test
    public void convertEntity() {
        OccurrenceEntity occurrence = OccurrenceEntity.builder()
            .occurrenceId(OCCURRENCE_ID_STRING)
            .eventId(EVENT_ID_STRING)
            .userId(USER_ID_STRING)
            .date(ENCRYPTED_DATE)
            .time(ENCRYPTED_TIME)
            .status(OccurrenceStatus.SNOOZED)
            .note(ENCRYPTED_NOTE)
            .type(ENCRYPTED_TYPE)
            .build();

        given(uuidConverter.convertEntity(OCCURRENCE_ID_STRING)).willReturn(OCCURRENCE_ID);
        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(stringEncryptor.decrypt(ENCRYPTED_DATE, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_DATE)).willReturn(DATE.toString());
        given(stringEncryptor.decrypt(ENCRYPTED_TIME, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_TIME)).willReturn(TIME.toString());
        given(stringEncryptor.decrypt(ENCRYPTED_NOTE, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_NOTE)).willReturn(NOTE);
        given(stringEncryptor.decrypt(ENCRYPTED_TYPE, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_TYPE)).willReturn(OccurrenceType.FOLLOW_UP.name());

        Occurrence result = underTest.processEntityConversion(occurrence);

        assertThat(result.getOccurrenceId()).isEqualTo(OCCURRENCE_ID);
        assertThat(result.getEventId()).isEqualTo(EVENT_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getDate()).isEqualTo(DATE);
        assertThat(result.getTime()).isEqualTo(TIME);
        assertThat(result.getStatus()).isEqualTo(OccurrenceStatus.SNOOZED);
        assertThat(result.getNote()).isEqualTo(NOTE);
        assertThat(result.getType()).isEqualTo(OccurrenceType.FOLLOW_UP);
    }

    @Test
    public void convertEntity_nullType() {
        OccurrenceEntity occurrence = OccurrenceEntity.builder()
            .occurrenceId(OCCURRENCE_ID_STRING)
            .eventId(EVENT_ID_STRING)
            .userId(USER_ID_STRING)
            .date(ENCRYPTED_DATE)
            .time(ENCRYPTED_TIME)
            .status(OccurrenceStatus.SNOOZED)
            .note(ENCRYPTED_NOTE)
            .type(null)
            .build();

        given(uuidConverter.convertEntity(OCCURRENCE_ID_STRING)).willReturn(OCCURRENCE_ID);
        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(stringEncryptor.decrypt(ENCRYPTED_DATE, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_DATE)).willReturn(DATE.toString());
        given(stringEncryptor.decrypt(ENCRYPTED_TIME, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_TIME)).willReturn(TIME.toString());
        given(stringEncryptor.decrypt(ENCRYPTED_NOTE, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_NOTE)).willReturn(NOTE);

        Occurrence result = underTest.convertEntity(occurrence);

        assertThat(result.getOccurrenceId()).isEqualTo(OCCURRENCE_ID);
        assertThat(result.getEventId()).isEqualTo(EVENT_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getDate()).isEqualTo(DATE);
        assertThat(result.getTime()).isEqualTo(TIME);
        assertThat(result.getStatus()).isEqualTo(OccurrenceStatus.SNOOZED);
        assertThat(result.getNote()).isEqualTo(NOTE);
        assertThat(result.getType()).isEqualTo(OccurrenceType.DEFAULT);
    }
}