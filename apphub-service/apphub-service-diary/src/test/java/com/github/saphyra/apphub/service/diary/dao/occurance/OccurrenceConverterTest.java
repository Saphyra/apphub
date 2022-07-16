package com.github.saphyra.apphub.service.diary.dao.occurance;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
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
    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @InjectMocks
    private OccurrenceConverter underTest;

    @Test
    public void convertDomain() {
        Occurrence occurrence = Occurrence.builder()
            .occurrenceId(OCCURRENCE_ID)
            .eventId(EVENT_ID)
            .userId(USER_ID)
            .date(DATE)
            .status(OccurrenceStatus.SNOOZED)
            .note(NOTE)
            .build();

        given(uuidConverter.convertDomain(OCCURRENCE_ID)).willReturn(OCCURRENCE_ID_STRING);
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(stringEncryptor.encryptEntity(DATE.toString(), USER_ID_STRING)).willReturn(ENCRYPTED_DATE);
        given(stringEncryptor.encryptEntity(NOTE, USER_ID_STRING)).willReturn(ENCRYPTED_NOTE);

        OccurrenceEntity result = underTest.convertDomain(occurrence);

        assertThat(result.getOccurrenceId()).isEqualTo(OCCURRENCE_ID_STRING);
        assertThat(result.getEventId()).isEqualTo(EVENT_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getDate()).isEqualTo(ENCRYPTED_DATE);
        assertThat(result.getStatus()).isEqualTo(OccurrenceStatus.SNOOZED);
        assertThat(result.getNote()).isEqualTo(ENCRYPTED_NOTE);
    }

    @Test
    public void convertEntity() {
        OccurrenceEntity occurrence = OccurrenceEntity.builder()
            .occurrenceId(OCCURRENCE_ID_STRING)
            .eventId(EVENT_ID_STRING)
            .userId(USER_ID_STRING)
            .date(ENCRYPTED_DATE)
            .status(OccurrenceStatus.SNOOZED)
            .note(ENCRYPTED_NOTE)
            .build();

        given(uuidConverter.convertEntity(OCCURRENCE_ID_STRING)).willReturn(OCCURRENCE_ID);
        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(stringEncryptor.decryptEntity(ENCRYPTED_DATE, USER_ID_STRING)).willReturn(DATE.toString());
        given(stringEncryptor.decryptEntity(ENCRYPTED_NOTE, USER_ID_STRING)).willReturn(NOTE);

        Occurrence result = underTest.convertEntity(occurrence);

        assertThat(result.getOccurrenceId()).isEqualTo(OCCURRENCE_ID);
        assertThat(result.getEventId()).isEqualTo(EVENT_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getDate()).isEqualTo(DATE);
        assertThat(result.getStatus()).isEqualTo(OccurrenceStatus.SNOOZED);
        assertThat(result.getNote()).isEqualTo(NOTE);
    }
}