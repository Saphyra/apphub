package com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.*;
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
@Deprecated(forRemoval = true)
class DeprecatedEventConverterTest {
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
    private static final String ENCRYPTED_EXPIRATION_NOTIFIED = "encrypted-expiration-notified";
    private static final String ENCRYPTED_ARCHIVED = "encrypted-archived";

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

    @Mock
    private BooleanEncryptor booleanEncryptor;

    @InjectMocks
    private DeprecatedEventConverter underTest;

    @Test
    void convertDomain() {
        DeprecatedEvent event = DeprecatedEvent.builder()
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
            .expirationNotified(true)
            .archived(true)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(stringEncryptor.encrypt(RepetitionType.EVERY_X_DAYS.name(), USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_REPETITION_TYPE)).willReturn(ENCRYPTED_REPETITION_TYPE);
        given(stringEncryptor.encrypt(REPETITION_DATA, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_REPETITION_DATA)).willReturn(ENCRYPTED_REPETITION_DATA);
        given(integerEncryptor.encrypt(REPEAT_FOR_DAYS, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_REPEAT_FOR_DAYS)).willReturn(ENCRYPTED_REPEAT_FOR_DAYS);
        given(localDateEncryptor.encrypt(START_DATE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_START_DATE)).willReturn(ENCRYPTED_START_DATE);
        given(localDateEncryptor.encrypt(END_DATE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_END_DATE)).willReturn(ENCRYPTED_END_DATE);
        given(localTimeEncryptor.encrypt(TIME, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_TIME)).willReturn(ENCRYPTED_TIME);
        given(stringEncryptor.encrypt(TITLE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_TITLE)).willReturn(ENCRYPTED_TITLE);
        given(stringEncryptor.encrypt(CONTENT, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_CONTENT)).willReturn(ENCRYPTED_CONTENT);
        given(integerEncryptor.encrypt(REMIND_ME_BEFORE_DAYS, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.REMIND_ME_BEFORE_DAYS)).willReturn(ENCRYPTED_REMIND_ME_BEFORE_DAYS);
        given(booleanEncryptor.encrypt(true, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.EXPIRATION_NOTIFIED)).willReturn(ENCRYPTED_EXPIRATION_NOTIFIED);
        given(booleanEncryptor.encrypt(true, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.ARCHIVED)).willReturn(ENCRYPTED_ARCHIVED);

        assertThat(underTest.convertDomain(event))
            .returns(EVENT_ID_STRING, DeprecatedEventEntity::getEventId)
            .returns(USER_ID_STRING, DeprecatedEventEntity::getUserId)
            .returns(ENCRYPTED_REPETITION_TYPE, DeprecatedEventEntity::getRepetitionType)
            .returns(ENCRYPTED_REPETITION_DATA, DeprecatedEventEntity::getRepetitionData)
            .returns(ENCRYPTED_REPEAT_FOR_DAYS, DeprecatedEventEntity::getRepeatForDays)
            .returns(ENCRYPTED_START_DATE, DeprecatedEventEntity::getStartDate)
            .returns(ENCRYPTED_END_DATE, DeprecatedEventEntity::getEndDate)
            .returns(ENCRYPTED_TIME, DeprecatedEventEntity::getTime)
            .returns(ENCRYPTED_TITLE, DeprecatedEventEntity::getTitle)
            .returns(ENCRYPTED_CONTENT, DeprecatedEventEntity::getContent)
            .returns(ENCRYPTED_REMIND_ME_BEFORE_DAYS, DeprecatedEventEntity::getRemindMeBeforeDays)
            .returns(ENCRYPTED_EXPIRATION_NOTIFIED, DeprecatedEventEntity::getExpirationNotified)
            .returns(ENCRYPTED_ARCHIVED, DeprecatedEventEntity::getArchived);
    }

    @Test
    void convertEntity() {
        DeprecatedEventEntity entity = DeprecatedEventEntity.builder()
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
            .expirationNotified(ENCRYPTED_EXPIRATION_NOTIFIED)
            .archived(ENCRYPTED_ARCHIVED)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(stringEncryptor.decrypt(ENCRYPTED_REPETITION_TYPE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_REPETITION_TYPE)).willReturn(RepetitionType.EVERY_X_DAYS.name());
        given(stringEncryptor.decrypt(ENCRYPTED_REPETITION_DATA, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_REPETITION_DATA)).willReturn(REPETITION_DATA);
        given(integerEncryptor.decrypt(ENCRYPTED_REPEAT_FOR_DAYS, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_REPEAT_FOR_DAYS)).willReturn(REPEAT_FOR_DAYS);
        given(localDateEncryptor.decrypt(ENCRYPTED_START_DATE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_START_DATE)).willReturn(START_DATE);
        given(localDateEncryptor.decrypt(ENCRYPTED_END_DATE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_END_DATE)).willReturn(END_DATE);
        given(localTimeEncryptor.decrypt(ENCRYPTED_TIME, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_TIME)).willReturn(TIME);
        given(stringEncryptor.decrypt(ENCRYPTED_TITLE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_TITLE)).willReturn(TITLE);
        given(stringEncryptor.decrypt(ENCRYPTED_CONTENT, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_CONTENT)).willReturn(CONTENT);
        given(integerEncryptor.decrypt(ENCRYPTED_REMIND_ME_BEFORE_DAYS, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.REMIND_ME_BEFORE_DAYS)).willReturn(REMIND_ME_BEFORE_DAYS);
        given(booleanEncryptor.decrypt(ENCRYPTED_EXPIRATION_NOTIFIED, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.EXPIRATION_NOTIFIED)).willReturn(true);
        given(booleanEncryptor.decrypt(ENCRYPTED_ARCHIVED, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.ARCHIVED)).willReturn(true);

        assertThat(underTest.convertEntity(entity))
            .returns(EVENT_ID, DeprecatedEvent::getEventId)
            .returns(USER_ID, DeprecatedEvent::getUserId)
            .returns(RepetitionType.EVERY_X_DAYS, DeprecatedEvent::getRepetitionType)
            .returns(REPETITION_DATA, DeprecatedEvent::getRepetitionData)
            .returns(REPEAT_FOR_DAYS, DeprecatedEvent::getRepeatForDays)
            .returns(START_DATE, DeprecatedEvent::getStartDate)
            .returns(END_DATE, DeprecatedEvent::getEndDate)
            .returns(TIME, DeprecatedEvent::getTime)
            .returns(TITLE, DeprecatedEvent::getTitle)
            .returns(CONTENT, DeprecatedEvent::getContent)
            .returns(REMIND_ME_BEFORE_DAYS, DeprecatedEvent::getRemindMeBeforeDays)
            .returns(true, DeprecatedEvent::isExpirationNotified)
            .returns(true, DeprecatedEvent::isArchived);
    }

    @Test
    void convertEntity_nulls() {
        DeprecatedEventEntity entity = DeprecatedEventEntity.builder()
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
            .expirationNotified(ENCRYPTED_EXPIRATION_NOTIFIED)
            .archived(ENCRYPTED_ARCHIVED)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(stringEncryptor.decrypt(ENCRYPTED_REPETITION_TYPE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_REPETITION_TYPE)).willReturn(RepetitionType.EVERY_X_DAYS.name());
        given(stringEncryptor.decrypt(ENCRYPTED_REPETITION_DATA, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_REPETITION_DATA)).willReturn(REPETITION_DATA);
        given(integerEncryptor.decrypt(ENCRYPTED_REPEAT_FOR_DAYS, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_REPEAT_FOR_DAYS)).willReturn(REPEAT_FOR_DAYS);
        given(localDateEncryptor.decrypt(ENCRYPTED_START_DATE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_START_DATE)).willReturn(START_DATE);
        given(localDateEncryptor.decrypt(ENCRYPTED_END_DATE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_END_DATE)).willReturn(END_DATE);
        given(localTimeEncryptor.decrypt(ENCRYPTED_TIME, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_TIME)).willReturn(TIME);
        given(stringEncryptor.decrypt(ENCRYPTED_TITLE, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_TITLE)).willReturn(TITLE);
        given(stringEncryptor.decrypt(ENCRYPTED_CONTENT, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.COLUMN_CONTENT)).willReturn(null);
        given(integerEncryptor.decrypt(ENCRYPTED_REMIND_ME_BEFORE_DAYS, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.REMIND_ME_BEFORE_DAYS)).willReturn(REMIND_ME_BEFORE_DAYS);
        given(booleanEncryptor.decrypt(ENCRYPTED_EXPIRATION_NOTIFIED, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.EXPIRATION_NOTIFIED)).willReturn(null);
        given(booleanEncryptor.decrypt(ENCRYPTED_ARCHIVED, USER_ID_FROM_ACCESS_TOKEN, EVENT_ID_STRING, DeprecatedEventConverter.ARCHIVED)).willReturn(null);

        assertThat(underTest.convertEntity(entity))
            .returns(EVENT_ID, DeprecatedEvent::getEventId)
            .returns(USER_ID, DeprecatedEvent::getUserId)
            .returns(RepetitionType.EVERY_X_DAYS, DeprecatedEvent::getRepetitionType)
            .returns(REPETITION_DATA, DeprecatedEvent::getRepetitionData)
            .returns(REPEAT_FOR_DAYS, DeprecatedEvent::getRepeatForDays)
            .returns(START_DATE, DeprecatedEvent::getStartDate)
            .returns(END_DATE, DeprecatedEvent::getEndDate)
            .returns(TIME, DeprecatedEvent::getTime)
            .returns(TITLE, DeprecatedEvent::getTitle)
            .returns(Constants.EMPTY_STRING, DeprecatedEvent::getContent)
            .returns(REMIND_ME_BEFORE_DAYS, DeprecatedEvent::getRemindMeBeforeDays)
            .returns(false, DeprecatedEvent::isExpirationNotified)
            .returns(false, DeprecatedEvent::isArchived);
    }
}