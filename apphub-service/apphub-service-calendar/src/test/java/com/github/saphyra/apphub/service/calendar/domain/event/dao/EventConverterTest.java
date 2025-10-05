package com.github.saphyra.apphub.service.calendar.domain.event.dao;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.LocalDateEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.LocalTimeEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
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
    private static final String REPETITION_DATA = "repetition-data";
    private static final Integer REPEAT_FOR_DAYS = 42;
    private static final LocalDate START_DATE = LocalDate.now();
    private static final LocalDate END_DATE = LocalDate.now().plusDays(1);
    private static final LocalTime TIME = LocalTime.now();
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final Integer REMIND_ME_BEFORE_DAYS = 7;
    private static final String USER_ID_FROM_ACCESS_TOKEN = "user-id-from-access-token";
    private static final String EVENT_ID_STRING = "event-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String ENCRYPTED_REPETITION_TYPE = "encrypted-repetition-type";
    private static final String ENCRYPTED_REPETITION_DATA = "encrypted-repetition-data";
    private static final String ENCRYPTED_REPEAT_FOR_DAYS = "encrypted-repeat-for-days";
    private static final String ENCRYPTED_START_DATE = "encrypted-start-date";
    private static final String ENCRYPTED_END_DATE = "encrypted-end-date";
    private static final String ENCRYPTED_TIME = "encrypted-time";
    private static final String ENCRYPTED_TITLE = "encrypted-title";
    private static final String ENCRYPTED_CONTENT = "encrypted-content";
    private static final String ENCRYPTED_REMIND_ME_BEFORE_DAYS = "encrypted-remind-me-before-days";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private IntegerEncryptor integerEncryptor;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private LocalDateEncryptor localDateEncryptor;

    @Mock
    private LocalTimeEncryptor localTimeEncryptor;

    @InjectMocks
    private EventConverter underTest;

    @Test
    void convertDomain() {
        Event event = Event.builder()
            .eventId(EVENT_ID)
            .userId(USER_ID)
            .repetitionType(RepetitionType.EVERY_X_DAYS)
            .repetitionData(REPETITION_DATA)
            .repeatForDays(REPEAT_FOR_DAYS)
            .startDate(START_DATE)
            .endDate(END_DATE)
            .time(TIME)
            .title(TITLE)
            .content(CONTENT)
            .remindMeBeforeDays(REMIND_ME_BEFORE_DAYS)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(stringEncryptor.encrypt(RepetitionType.EVERY_X_DAYS.name(), USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_REPETITION_TYPE)).willReturn(ENCRYPTED_REPETITION_TYPE);
        given(stringEncryptor.encrypt(REPETITION_DATA, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_REPETITION_DATA)).willReturn(ENCRYPTED_REPETITION_DATA);
        given(integerEncryptor.encrypt(REPEAT_FOR_DAYS, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_REPEAT_FOR_DAYS)).willReturn(ENCRYPTED_REPEAT_FOR_DAYS);
        given(localDateEncryptor.encrypt(START_DATE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_START_DATE)).willReturn(ENCRYPTED_START_DATE);
        given(localDateEncryptor.encrypt(END_DATE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_END_DATE)).willReturn(ENCRYPTED_END_DATE);
        given(localTimeEncryptor.encrypt(TIME, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_TIME)).willReturn(ENCRYPTED_TIME);
        given(stringEncryptor.encrypt(TITLE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_TITLE)).willReturn(ENCRYPTED_TITLE);
        given(stringEncryptor.encrypt(CONTENT, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_CONTENT)).willReturn(ENCRYPTED_CONTENT);
        given(integerEncryptor.encrypt(REMIND_ME_BEFORE_DAYS, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.REMIND_ME_BEFORE_DAYS)).willReturn(ENCRYPTED_REMIND_ME_BEFORE_DAYS);

        assertThat(underTest.convertDomain(event))
            .returns(EVENT_ID_STRING, EventEntity::getEventId)
            .returns(USER_ID_STRING, EventEntity::getUserId)
            .returns(ENCRYPTED_REPETITION_TYPE, EventEntity::getRepetitionType)
            .returns(ENCRYPTED_REPETITION_DATA, EventEntity::getRepetitionData)
            .returns(ENCRYPTED_REPEAT_FOR_DAYS, EventEntity::getRepeatForDays)
            .returns(ENCRYPTED_START_DATE, EventEntity::getStartDate)
            .returns(ENCRYPTED_END_DATE, EventEntity::getEndDate)
            .returns(ENCRYPTED_TIME, EventEntity::getTime)
            .returns(ENCRYPTED_TITLE, EventEntity::getTitle)
            .returns(ENCRYPTED_CONTENT, EventEntity::getContent)
            .returns(ENCRYPTED_REMIND_ME_BEFORE_DAYS, EventEntity::getRemindMeBeforeDays);
    }

    @Test
    void convertEntity() {
        EventEntity entity = EventEntity.builder()
            .eventId(EVENT_ID_STRING)
            .userId(USER_ID_STRING)
            .repetitionType(ENCRYPTED_REPETITION_TYPE)
            .repetitionData(ENCRYPTED_REPETITION_DATA)
            .repeatForDays(ENCRYPTED_REPEAT_FOR_DAYS)
            .startDate(ENCRYPTED_START_DATE)
            .endDate(ENCRYPTED_END_DATE)
            .time(ENCRYPTED_TIME)
            .title(ENCRYPTED_TITLE)
            .content(ENCRYPTED_CONTENT)
            .remindMeBeforeDays(ENCRYPTED_REMIND_ME_BEFORE_DAYS)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(stringEncryptor.decrypt(ENCRYPTED_REPETITION_TYPE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_REPETITION_TYPE)).willReturn(RepetitionType.EVERY_X_DAYS.name());
        given(stringEncryptor.decrypt(ENCRYPTED_REPETITION_DATA, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_REPETITION_DATA)).willReturn(REPETITION_DATA);
        given(integerEncryptor.decrypt(ENCRYPTED_REPEAT_FOR_DAYS, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_REPEAT_FOR_DAYS)).willReturn(REPEAT_FOR_DAYS);
        given(localDateEncryptor.decrypt(ENCRYPTED_START_DATE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_START_DATE)).willReturn(START_DATE);
        given(localDateEncryptor.decrypt(ENCRYPTED_END_DATE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_END_DATE)).willReturn(END_DATE);
        given(localTimeEncryptor.decrypt(ENCRYPTED_TIME, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_TIME)).willReturn(TIME);
        given(stringEncryptor.decrypt(ENCRYPTED_TITLE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_TITLE)).willReturn(TITLE);
        given(stringEncryptor.decrypt(ENCRYPTED_CONTENT, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_CONTENT)).willReturn(CONTENT);
        given(integerEncryptor.decrypt(ENCRYPTED_REMIND_ME_BEFORE_DAYS, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.REMIND_ME_BEFORE_DAYS)).willReturn(REMIND_ME_BEFORE_DAYS);

        assertThat(underTest.convertEntity(entity))
            .returns(EVENT_ID, Event::getEventId)
            .returns(USER_ID, Event::getUserId)
            .returns(RepetitionType.EVERY_X_DAYS, Event::getRepetitionType)
            .returns(REPETITION_DATA, Event::getRepetitionData)
            .returns(REPEAT_FOR_DAYS, Event::getRepeatForDays)
            .returns(START_DATE, Event::getStartDate)
            .returns(END_DATE, Event::getEndDate)
            .returns(TIME, Event::getTime)
            .returns(TITLE, Event::getTitle)
            .returns(CONTENT, Event::getContent)
            .returns(REMIND_ME_BEFORE_DAYS, Event::getRemindMeBeforeDays);
    }

    @Test
    void convertEntity_nulls() {
        EventEntity entity = EventEntity.builder()
            .eventId(EVENT_ID_STRING)
            .userId(USER_ID_STRING)
            .repetitionType(ENCRYPTED_REPETITION_TYPE)
            .repetitionData(ENCRYPTED_REPETITION_DATA)
            .repeatForDays(ENCRYPTED_REPEAT_FOR_DAYS)
            .startDate(ENCRYPTED_START_DATE)
            .endDate(ENCRYPTED_END_DATE)
            .time(ENCRYPTED_TIME)
            .title(ENCRYPTED_TITLE)
            .content(ENCRYPTED_CONTENT)
            .remindMeBeforeDays(ENCRYPTED_REMIND_ME_BEFORE_DAYS)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(stringEncryptor.decrypt(ENCRYPTED_REPETITION_TYPE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_REPETITION_TYPE)).willReturn(RepetitionType.EVERY_X_DAYS.name());
        given(stringEncryptor.decrypt(ENCRYPTED_REPETITION_DATA, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_REPETITION_DATA)).willReturn(REPETITION_DATA);
        given(integerEncryptor.decrypt(ENCRYPTED_REPEAT_FOR_DAYS, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_REPEAT_FOR_DAYS)).willReturn(REPEAT_FOR_DAYS);
        given(localDateEncryptor.decrypt(ENCRYPTED_START_DATE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_START_DATE)).willReturn(START_DATE);
        given(localDateEncryptor.decrypt(ENCRYPTED_END_DATE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_END_DATE)).willReturn(END_DATE);
        given(localTimeEncryptor.decrypt(ENCRYPTED_TIME, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_TIME)).willReturn(TIME);
        given(stringEncryptor.decrypt(ENCRYPTED_TITLE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_TITLE)).willReturn(TITLE);
        given(stringEncryptor.decrypt(ENCRYPTED_CONTENT, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.COLUMN_CONTENT)).willReturn(null);
        given(integerEncryptor.decrypt(ENCRYPTED_REMIND_ME_BEFORE_DAYS, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, EventConverter.REMIND_ME_BEFORE_DAYS)).willReturn(REMIND_ME_BEFORE_DAYS);

        assertThat(underTest.convertEntity(entity))
            .returns(EVENT_ID, Event::getEventId)
            .returns(USER_ID, Event::getUserId)
            .returns(RepetitionType.EVERY_X_DAYS, Event::getRepetitionType)
            .returns(REPETITION_DATA, Event::getRepetitionData)
            .returns(REPEAT_FOR_DAYS, Event::getRepeatForDays)
            .returns(START_DATE, Event::getStartDate)
            .returns(END_DATE, Event::getEndDate)
            .returns(TIME, Event::getTime)
            .returns(TITLE, Event::getTitle)
            .returns(Constants.EMPTY_STRING, Event::getContent)
            .returns(REMIND_ME_BEFORE_DAYS, Event::getRemindMeBeforeDays);
    }
}