package com.github.saphyra.apphub.service.calendar.dao.event;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
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

import static com.github.saphyra.apphub.service.calendar.dao.event.EventConverter.COLUMN_CONTENT;
import static com.github.saphyra.apphub.service.calendar.dao.event.EventConverter.COLUMN_REPEAT;
import static com.github.saphyra.apphub.service.calendar.dao.event.EventConverter.COLUMN_REPETITION_DATA;
import static com.github.saphyra.apphub.service.calendar.dao.event.EventConverter.COLUMN_START_DATE;
import static com.github.saphyra.apphub.service.calendar.dao.event.EventConverter.COLUMN_TIME;
import static com.github.saphyra.apphub.service.calendar.dao.event.EventConverter.COLUMN_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
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
    private static final LocalTime TIME = LocalTime.now();
    private static final String ENCRYPTED_TIME = "encrypted-time";
    private static final Integer REPEAT = 345;
    private static final String ENCRYPTED_REPEAT = "encrypted-repeat";
    private static final String ACCESS_TOKEN_USER_ID = "access-token-user-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private IntegerEncryptor integerEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private EventConverter underTest;

    @BeforeEach
    public void setUp() {
        given(accessTokenProvider.getUserIdAsString()).willReturn(ACCESS_TOKEN_USER_ID);
    }

    @Test
    public void convertDomain() {
        Event event = Event.builder()
            .eventId(EVENT_ID)
            .userId(USER_ID)
            .startDate(START_DATE)
            .time(TIME)
            .repetitionType(RepetitionType.DAYS_OF_WEEK)
            .repetitionData(REPETITION_DATA)
            .title(TITLE)
            .content(CONTENT)
            .repeat(REPEAT)
            .build();

        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(stringEncryptor.encrypt(START_DATE.toString(), ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_START_DATE)).willReturn(ENCRYPTED_START_DATE);
        given(stringEncryptor.encrypt(TIME.toString(), ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_TIME)).willReturn(ENCRYPTED_TIME);
        given(stringEncryptor.encrypt(REPETITION_DATA, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_REPETITION_DATA)).willReturn(ENCRYPTED_REPETITION_DATA);
        given(stringEncryptor.encrypt(TITLE, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_TITLE)).willReturn(ENCRYPTED_TITLE);
        given(stringEncryptor.encrypt(CONTENT, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_CONTENT)).willReturn(ENCRYPTED_CONTENT);
        given(integerEncryptor.encrypt(REPEAT, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_REPEAT)).willReturn(ENCRYPTED_REPEAT);

        EventEntity result = underTest.convertDomain(event);

        assertThat(result.getEventId()).isEqualTo(EVENT_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getStartDate()).isEqualTo(ENCRYPTED_START_DATE);
        assertThat(result.getTime()).isEqualTo(ENCRYPTED_TIME);
        assertThat(result.getRepetitionType()).isEqualTo(RepetitionType.DAYS_OF_WEEK);
        assertThat(result.getRepetitionData()).isEqualTo(ENCRYPTED_REPETITION_DATA);
        assertThat(result.getTitle()).isEqualTo(ENCRYPTED_TITLE);
        assertThat(result.getContent()).isEqualTo(ENCRYPTED_CONTENT);
        assertThat(result.getRepeat()).isEqualTo(ENCRYPTED_REPEAT);
    }

    @Test
    public void convertEntity() {
        EventEntity entity = EventEntity.builder()
            .eventId(EVENT_ID_STRING)
            .userId(USER_ID_STRING)
            .startDate(ENCRYPTED_START_DATE)
            .time(ENCRYPTED_TIME)
            .repetitionType(RepetitionType.DAYS_OF_WEEK)
            .repetitionData(ENCRYPTED_REPETITION_DATA)
            .title(ENCRYPTED_TITLE)
            .content(ENCRYPTED_CONTENT)
            .repeat(ENCRYPTED_REPEAT)
            .build();

        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(stringEncryptor.decrypt(ENCRYPTED_START_DATE, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_START_DATE)).willReturn(START_DATE.toString());
        given(stringEncryptor.decrypt(ENCRYPTED_TIME, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_TIME)).willReturn(TIME.toString());
        given(stringEncryptor.decrypt(ENCRYPTED_REPETITION_DATA, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_REPETITION_DATA)).willReturn(REPETITION_DATA);
        given(stringEncryptor.decrypt(ENCRYPTED_TITLE, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_TITLE)).willReturn(TITLE);
        given(stringEncryptor.decrypt(ENCRYPTED_CONTENT, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_CONTENT)).willReturn(CONTENT);
        given(integerEncryptor.decrypt(ENCRYPTED_REPEAT, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_REPEAT)).willReturn(REPEAT);

        Event result = underTest.convertEntity(entity);

        assertThat(result.getEventId()).isEqualTo(EVENT_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getStartDate()).isEqualTo(START_DATE);
        assertThat(result.getTime()).isEqualTo(TIME);
        assertThat(result.getRepetitionType()).isEqualTo(RepetitionType.DAYS_OF_WEEK);
        assertThat(result.getRepetitionData()).isEqualTo(REPETITION_DATA);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getContent()).isEqualTo(CONTENT);
        assertThat(result.getRepeat()).isEqualTo(REPEAT);
    }

    @Test
    public void convertEntity_nullRepeat() {
        EventEntity entity = EventEntity.builder()
            .eventId(EVENT_ID_STRING)
            .userId(USER_ID_STRING)
            .startDate(ENCRYPTED_START_DATE)
            .time(ENCRYPTED_TIME)
            .repetitionType(RepetitionType.DAYS_OF_WEEK)
            .repetitionData(ENCRYPTED_REPETITION_DATA)
            .title(ENCRYPTED_TITLE)
            .content(ENCRYPTED_CONTENT)
            .repeat(null)
            .build();

        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(stringEncryptor.decrypt(ENCRYPTED_START_DATE, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_START_DATE)).willReturn(START_DATE.toString());
        given(stringEncryptor.decrypt(ENCRYPTED_TIME, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_TIME)).willReturn(TIME.toString());
        given(stringEncryptor.decrypt(ENCRYPTED_REPETITION_DATA, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_REPETITION_DATA)).willReturn(REPETITION_DATA);
        given(stringEncryptor.decrypt(ENCRYPTED_TITLE, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_TITLE)).willReturn(TITLE);
        given(stringEncryptor.decrypt(ENCRYPTED_CONTENT, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_CONTENT)).willReturn(CONTENT);
        given(integerEncryptor.decrypt(null, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_REPEAT)).willReturn(null);

        Event result = underTest.processEntityConversion(entity);

        assertThat(result.getEventId()).isEqualTo(EVENT_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getStartDate()).isEqualTo(START_DATE);
        assertThat(result.getTime()).isEqualTo(TIME);
        assertThat(result.getRepetitionType()).isEqualTo(RepetitionType.DAYS_OF_WEEK);
        assertThat(result.getRepetitionData()).isEqualTo(REPETITION_DATA);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getContent()).isEqualTo(CONTENT);
        assertThat(result.getRepeat()).isEqualTo(1);
    }
}