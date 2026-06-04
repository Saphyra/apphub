package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
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

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_ARCHIVED;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_CONTENT;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_END_DATE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_EXPIRATION_NOTIFIED;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REMIND_ME_BEFORE_DAYS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REPEAT_FOR_DAYS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REPETITION_DATA;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REPETITION_TYPE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_START_DATE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_TIME;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EventConverterTest {
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EVENT_ID_STRING = "event-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String ACCESS_TOKEN_USER_ID = "access-token-user-id";
    private static final RepetitionType REPETITION_TYPE = RepetitionType.EVERY_X_DAYS;
    private static final String REPETITION_TYPE_ENCRYPTED = "repetition-type-encrypted";
    private static final String REPETITION_DATA = "repetition-data";
    private static final String REPETITION_DATA_ENCRYPTED = "repetition-data-encrypted";
    private static final Integer REPEAT_FOR_DAYS = 7;
    private static final String REPEAT_FOR_DAYS_ENCRYPTED = "repeat-for-days-encrypted";
    private static final LocalDate START_DATE = LocalDate.of(2026, 6, 3);
    private static final String START_DATE_ENCRYPTED = "start-date-encrypted";
    private static final LocalDate END_DATE = LocalDate.of(2026, 12, 31);
    private static final String END_DATE_ENCRYPTED = "end-date-encrypted";
    private static final LocalTime TIME = LocalTime.of(14, 30, 0);
    private static final String TIME_ENCRYPTED = "time-encrypted";
    private static final String TITLE = "Event Title";
    private static final String TITLE_ENCRYPTED = "title-encrypted";
    private static final String CONTENT = "Event Content";
    private static final String CONTENT_ENCRYPTED = "content-encrypted";
    private static final Integer REMIND_ME_BEFORE_DAYS = 2;
    private static final String REMIND_ME_BEFORE_DAYS_ENCRYPTED = "remind-me-before-days-encrypted";
    private static final Boolean EXPIRATION_NOTIFIED = true;
    private static final String EXPIRATION_NOTIFIED_ENCRYPTED = "expiration-notified-encrypted";
    private static final Boolean ARCHIVED = false;
    private static final String ARCHIVED_ENCRYPTED = "archived-encrypted";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private IntegerEncryptor integerEncryptor;

    @Mock
    private LocalDateEncryptor localDateEncryptor;

    @Mock
    private LocalTimeEncryptor localTimeEncryptor;

    @Mock
    private BooleanEncryptor booleanEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

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
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(ACCESS_TOKEN_USER_ID);
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(stringEncryptor.encrypt(REPETITION_TYPE.name(), ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_REPETITION_TYPE)).willReturn(REPETITION_TYPE_ENCRYPTED);
        given(stringEncryptor.encrypt(REPETITION_DATA, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_REPETITION_DATA)).willReturn(REPETITION_DATA_ENCRYPTED);
        given(integerEncryptor.encrypt(REPEAT_FOR_DAYS, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_REPEAT_FOR_DAYS)).willReturn(REPEAT_FOR_DAYS_ENCRYPTED);
        given(localDateEncryptor.encrypt(START_DATE, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_START_DATE)).willReturn(START_DATE_ENCRYPTED);
        given(localDateEncryptor.encrypt(END_DATE, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_END_DATE)).willReturn(END_DATE_ENCRYPTED);
        given(localTimeEncryptor.encrypt(TIME, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_TIME)).willReturn(TIME_ENCRYPTED);
        given(stringEncryptor.encrypt(TITLE, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_TITLE)).willReturn(TITLE_ENCRYPTED);
        given(stringEncryptor.encrypt(CONTENT, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_CONTENT)).willReturn(CONTENT_ENCRYPTED);
        given(integerEncryptor.encrypt(REMIND_ME_BEFORE_DAYS, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_REMIND_ME_BEFORE_DAYS)).willReturn(REMIND_ME_BEFORE_DAYS_ENCRYPTED);
        given(booleanEncryptor.encrypt(EXPIRATION_NOTIFIED, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_EXPIRATION_NOTIFIED)).willReturn(EXPIRATION_NOTIFIED_ENCRYPTED);
        given(booleanEncryptor.encrypt(ARCHIVED, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_ARCHIVED)).willReturn(ARCHIVED_ENCRYPTED);

        EventEntity result = underTest.convertDomain(domain);

        assertThat(result)
            .returns(EVENT_ID_STRING, EventEntity::getEventId)
            .returns(USER_ID_STRING, EventEntity::getUserId)
            .returns(REPETITION_TYPE_ENCRYPTED, EventEntity::getRepetitionType)
            .returns(REPETITION_DATA_ENCRYPTED, EventEntity::getRepetitionData)
            .returns(REPEAT_FOR_DAYS_ENCRYPTED, EventEntity::getRepeatForDays)
            .returns(START_DATE_ENCRYPTED, EventEntity::getStartDate)
            .returns(END_DATE_ENCRYPTED, EventEntity::getEndDate)
            .returns(TIME_ENCRYPTED, EventEntity::getTime)
            .returns(TITLE_ENCRYPTED, EventEntity::getTitle)
            .returns(CONTENT_ENCRYPTED, EventEntity::getContent)
            .returns(REMIND_ME_BEFORE_DAYS_ENCRYPTED, EventEntity::getRemindMeBeforeDays)
            .returns(EXPIRATION_NOTIFIED_ENCRYPTED, EventEntity::getExpirationNotified)
            .returns(ARCHIVED_ENCRYPTED, EventEntity::getArchived);
    }

    @Test
    void convertEntity() {
        EventEntity entity = EventEntity.builder()
            .eventId(EVENT_ID_STRING)
            .userId(USER_ID_STRING)
            .repetitionType(REPETITION_TYPE_ENCRYPTED)
            .repetitionData(REPETITION_DATA_ENCRYPTED)
            .repeatForDays(REPEAT_FOR_DAYS_ENCRYPTED)
            .startDate(START_DATE_ENCRYPTED)
            .endDate(END_DATE_ENCRYPTED)
            .time(TIME_ENCRYPTED)
            .title(TITLE_ENCRYPTED)
            .content(CONTENT_ENCRYPTED)
            .remindMeBeforeDays(REMIND_ME_BEFORE_DAYS_ENCRYPTED)
            .expirationNotified(EXPIRATION_NOTIFIED_ENCRYPTED)
            .archived(ARCHIVED_ENCRYPTED)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(ACCESS_TOKEN_USER_ID);
        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(stringEncryptor.decrypt(REPETITION_TYPE_ENCRYPTED, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_REPETITION_TYPE)).willReturn(REPETITION_TYPE.name());
        given(stringEncryptor.decrypt(REPETITION_DATA_ENCRYPTED, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_REPETITION_DATA)).willReturn(REPETITION_DATA);
        given(integerEncryptor.decrypt(REPEAT_FOR_DAYS_ENCRYPTED, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_REPEAT_FOR_DAYS)).willReturn(REPEAT_FOR_DAYS);
        given(localDateEncryptor.decrypt(START_DATE_ENCRYPTED, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_START_DATE)).willReturn(START_DATE);
        given(localDateEncryptor.decrypt(END_DATE_ENCRYPTED, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_END_DATE)).willReturn(END_DATE);
        given(localTimeEncryptor.decrypt(TIME_ENCRYPTED, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_TIME)).willReturn(TIME);
        given(stringEncryptor.decrypt(TITLE_ENCRYPTED, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_TITLE)).willReturn(TITLE);
        given(stringEncryptor.decrypt(CONTENT_ENCRYPTED, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_CONTENT)).willReturn(CONTENT);
        given(integerEncryptor.decrypt(REMIND_ME_BEFORE_DAYS_ENCRYPTED, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_REMIND_ME_BEFORE_DAYS)).willReturn(REMIND_ME_BEFORE_DAYS);
        given(booleanEncryptor.decrypt(EXPIRATION_NOTIFIED_ENCRYPTED, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_EXPIRATION_NOTIFIED)).willReturn(EXPIRATION_NOTIFIED);
        given(booleanEncryptor.decrypt(ARCHIVED_ENCRYPTED, ACCESS_TOKEN_USER_ID, EVENT_ID_STRING, COLUMN_ARCHIVED)).willReturn(ARCHIVED);

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
            .returns(ARCHIVED, Event::isArchived);
    }
}