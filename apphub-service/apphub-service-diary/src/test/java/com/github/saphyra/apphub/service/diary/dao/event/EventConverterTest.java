package com.github.saphyra.apphub.service.diary.dao.event;

import com.github.saphyra.apphub.api.diary.model.RepetitionType;
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
public class EventConverterTest {
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDate START_DATE = LocalDate.now();
    private static final String REPETITION_DATA = "repetition-data";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String EVENT_ID_STRING = "event-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String ENCRYPTED_START_DATE = "encrypted-start-date";
    private static final String ENCRYPTED_REPETITION_DATA = "encrypted-repetition-data";
    private static final String ENCRYPTED_TITLE = "encrypted-title";
    private static final String ENCRYPTED_CONTENT = "encrypted-content";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @InjectMocks
    private EventConverter underTest;

    @Test
    public void convertDomain() {
        Event event = Event.builder()
            .eventId(EVENT_ID)
            .userId(USER_ID)
            .startDate(START_DATE)
            .repetitionType(RepetitionType.DAYS_OF_WEEK)
            .repetitionData(REPETITION_DATA)
            .title(TITLE)
            .content(CONTENT)
            .build();

        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(stringEncryptor.encryptEntity(START_DATE.toString(), USER_ID_STRING)).willReturn(ENCRYPTED_START_DATE);
        given(stringEncryptor.encryptEntity(REPETITION_DATA, USER_ID_STRING)).willReturn(ENCRYPTED_REPETITION_DATA);
        given(stringEncryptor.encryptEntity(TITLE, USER_ID_STRING)).willReturn(ENCRYPTED_TITLE);
        given(stringEncryptor.encryptEntity(CONTENT, USER_ID_STRING)).willReturn(ENCRYPTED_CONTENT);

        EventEntity result = underTest.convertDomain(event);

        assertThat(result.getEventId()).isEqualTo(EVENT_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getStartDate()).isEqualTo(ENCRYPTED_START_DATE);
        assertThat(result.getRepetitionType()).isEqualTo(RepetitionType.DAYS_OF_WEEK);
        assertThat(result.getRepetitionData()).isEqualTo(ENCRYPTED_REPETITION_DATA);
        assertThat(result.getTitle()).isEqualTo(ENCRYPTED_TITLE);
        assertThat(result.getContent()).isEqualTo(ENCRYPTED_CONTENT);
    }

    @Test
    public void convertEntity() {
        EventEntity entity = EventEntity.builder()
            .eventId(EVENT_ID_STRING)
            .userId(USER_ID_STRING)
            .startDate(ENCRYPTED_START_DATE)
            .repetitionType(RepetitionType.DAYS_OF_WEEK)
            .repetitionData(ENCRYPTED_REPETITION_DATA)
            .title(ENCRYPTED_TITLE)
            .content(ENCRYPTED_CONTENT)
            .build();

        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(stringEncryptor.decryptEntity(ENCRYPTED_START_DATE, USER_ID_STRING)).willReturn(START_DATE.toString());
        given(stringEncryptor.decryptEntity(ENCRYPTED_REPETITION_DATA, USER_ID_STRING)).willReturn(REPETITION_DATA);
        given(stringEncryptor.decryptEntity(ENCRYPTED_TITLE, USER_ID_STRING)).willReturn(TITLE);
        given(stringEncryptor.decryptEntity(ENCRYPTED_CONTENT, USER_ID_STRING)).willReturn(CONTENT);

        Event result = underTest.convertEntity(entity);

        assertThat(result.getEventId()).isEqualTo(EVENT_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getStartDate()).isEqualTo(START_DATE);
        assertThat(result.getRepetitionType()).isEqualTo(RepetitionType.DAYS_OF_WEEK);
        assertThat(result.getRepetitionData()).isEqualTo(REPETITION_DATA);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getContent()).isEqualTo(CONTENT);
    }
}