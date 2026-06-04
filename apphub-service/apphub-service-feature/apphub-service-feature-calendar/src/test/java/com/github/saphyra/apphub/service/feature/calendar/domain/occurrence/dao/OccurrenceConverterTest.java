package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
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

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_DATE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_NOTE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REMIND_ME_BEFORE_DAYS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REMINDED;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_STATUS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OccurrenceConverterTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final String EVENT_ID_STRING = "event-id";
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final String OCCURRENCE_ID_STRING = "occurrence-id";
    private static final String ACCESS_TOKEN_USER_ID = "access-token-user-id";
    private static final LocalDate DATE = LocalDate.now();
    private static final LocalTime TIME = LocalTime.of(14, 15);
    private static final String ENCRYPTED_DATE = "encrypted-date";
    private static final String ENCRYPTED_TIME = "encrypted-time";
    private static final String ENCRYPTED_STATUS = "encrypted-status";
    private static final String ENCRYPTED_NOTE = "encrypted-note";
    private static final String ENCRYPTED_REMIND_ME_BEFORE_DAYS = "encrypted-remind-me-before-days";
    private static final String ENCRYPTED_REMINDED = "encrypted-reminded";
    private static final String NOTE = "note";
    private static final Integer REMIND_ME_BEFORE_DAYS = 3;
    private static final String ENCRYPTED_NEW_STATUS = "encrypted-new-status";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private LocalDateEncryptor localDateEncryptor;

    @Mock
    private LocalTimeEncryptor localTimeEncryptor;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private IntegerEncryptor integerEncryptor;

    @Mock
    private BooleanEncryptor booleanEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private OccurrenceRepository occurrenceRepository;

    @InjectMocks
    private OccurrenceConverter underTest;

    @Test
    void convertDomain() {
        Occurrence domain = Occurrence.builder()
            .userId(USER_ID)
            .eventId(EVENT_ID)
            .occurrenceId(OCCURRENCE_ID)
            .date(DATE)
            .time(TIME)
            .status(OccurrenceStatus.DONE)
            .note(NOTE)
            .remindMeBeforeDays(REMIND_ME_BEFORE_DAYS)
            .reminded(true)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(ACCESS_TOKEN_USER_ID);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(uuidConverter.convertDomain(OCCURRENCE_ID)).willReturn(OCCURRENCE_ID_STRING);
        given(localDateEncryptor.encrypt(DATE, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_DATE)).willReturn(ENCRYPTED_DATE);
        given(localTimeEncryptor.encrypt(TIME, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_TIME)).willReturn(ENCRYPTED_TIME);
        given(stringEncryptor.encrypt(OccurrenceStatus.DONE.name(), ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_STATUS)).willReturn(ENCRYPTED_STATUS);
        given(stringEncryptor.encrypt(NOTE, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_NOTE)).willReturn(ENCRYPTED_NOTE);
        given(integerEncryptor.encrypt(REMIND_ME_BEFORE_DAYS, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_REMIND_ME_BEFORE_DAYS)).willReturn(ENCRYPTED_REMIND_ME_BEFORE_DAYS);
        given(booleanEncryptor.encrypt(true, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_REMINDED)).willReturn(ENCRYPTED_REMINDED);

        assertThat(underTest.convertDomain(domain))
            .returns(USER_ID_STRING, OccurrenceEntity::getUserId)
            .returns(EVENT_ID_STRING, OccurrenceEntity::getEventId)
            .returns(OCCURRENCE_ID_STRING, OccurrenceEntity::getOccurrenceId)
            .returns(DATE.getYear() + "-" + DATE.getMonthValue(), OccurrenceEntity::getDateBucket)
            .returns(ENCRYPTED_DATE, OccurrenceEntity::getDate)
            .returns(ENCRYPTED_TIME, OccurrenceEntity::getTime)
            .returns(ENCRYPTED_STATUS, OccurrenceEntity::getStatus)
            .returns(ENCRYPTED_NOTE, OccurrenceEntity::getNote)
            .returns(ENCRYPTED_REMIND_ME_BEFORE_DAYS, OccurrenceEntity::getRemindMeBeforeDays)
            .returns(ENCRYPTED_REMINDED, OccurrenceEntity::getReminded);
    }

    @Test
    void convertEntity() {
        OccurrenceEntity entity = OccurrenceEntity.builder()
            .userId(USER_ID_STRING)
            .eventId(EVENT_ID_STRING)
            .occurrenceId(OCCURRENCE_ID_STRING)
            .date(ENCRYPTED_DATE)
            .time(ENCRYPTED_TIME)
            .status(ENCRYPTED_STATUS)
            .note(ENCRYPTED_NOTE)
            .remindMeBeforeDays(ENCRYPTED_REMIND_ME_BEFORE_DAYS)
            .reminded(ENCRYPTED_REMINDED)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(ACCESS_TOKEN_USER_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(OCCURRENCE_ID_STRING)).willReturn(OCCURRENCE_ID);
        given(localDateEncryptor.decrypt(ENCRYPTED_DATE, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_DATE)).willReturn(DATE);
        given(localTimeEncryptor.decrypt(ENCRYPTED_TIME, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_TIME)).willReturn(TIME);
        given(stringEncryptor.decrypt(ENCRYPTED_STATUS, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_STATUS)).willReturn(OccurrenceStatus.PENDING.name());
        given(stringEncryptor.decrypt(ENCRYPTED_NOTE, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_NOTE)).willReturn(NOTE);
        given(integerEncryptor.decrypt(ENCRYPTED_REMIND_ME_BEFORE_DAYS, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_REMIND_ME_BEFORE_DAYS)).willReturn(REMIND_ME_BEFORE_DAYS);
        given(booleanEncryptor.decrypt(ENCRYPTED_REMINDED, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_REMINDED)).willReturn(false);
        given(dateTimeUtil.getCurrentDate()).willReturn(DATE.minusDays(1));

        assertThat(underTest.convertEntity(entity))
            .returns(USER_ID, Occurrence::getUserId)
            .returns(EVENT_ID, Occurrence::getEventId)
            .returns(OCCURRENCE_ID, Occurrence::getOccurrenceId)
            .returns(DATE, Occurrence::getDate)
            .returns(TIME, Occurrence::getTime)
            .returns(OccurrenceStatus.PENDING, Occurrence::getStatus)
            .returns(NOTE, Occurrence::getNote)
            .returns(REMIND_ME_BEFORE_DAYS, Occurrence::getRemindMeBeforeDays)
            .returns(false, Occurrence::isReminded);
    }

    @Test
    void convertEntity_statusExpired() {
        OccurrenceEntity entity = OccurrenceEntity.builder()
            .userId(USER_ID_STRING)
            .eventId(EVENT_ID_STRING)
            .occurrenceId(OCCURRENCE_ID_STRING)
            .date(ENCRYPTED_DATE)
            .time(ENCRYPTED_TIME)
            .status(ENCRYPTED_STATUS)
            .note(ENCRYPTED_NOTE)
            .remindMeBeforeDays(ENCRYPTED_REMIND_ME_BEFORE_DAYS)
            .reminded(ENCRYPTED_REMINDED)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(ACCESS_TOKEN_USER_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(OCCURRENCE_ID_STRING)).willReturn(OCCURRENCE_ID);
        given(localDateEncryptor.decrypt(ENCRYPTED_DATE, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_DATE)).willReturn(DATE);
        given(localTimeEncryptor.decrypt(ENCRYPTED_TIME, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_TIME)).willReturn(TIME);
        given(stringEncryptor.decrypt(ENCRYPTED_STATUS, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_STATUS)).willReturn(OccurrenceStatus.PENDING.name());
        given(stringEncryptor.decrypt(ENCRYPTED_NOTE, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_NOTE)).willReturn(NOTE);
        given(integerEncryptor.decrypt(ENCRYPTED_REMIND_ME_BEFORE_DAYS, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_REMIND_ME_BEFORE_DAYS)).willReturn(REMIND_ME_BEFORE_DAYS);
        given(booleanEncryptor.decrypt(ENCRYPTED_REMINDED, ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_REMINDED)).willReturn(false);
        given(dateTimeUtil.getCurrentDate()).willReturn(DATE.plusDays(1));
        given(stringEncryptor.encrypt(OccurrenceStatus.EXPIRED.name(), ACCESS_TOKEN_USER_ID, OCCURRENCE_ID_STRING, COLUMN_STATUS)).willReturn(ENCRYPTED_NEW_STATUS);

        assertThat(underTest.convertEntity(entity))
            .returns(USER_ID, Occurrence::getUserId)
            .returns(EVENT_ID, Occurrence::getEventId)
            .returns(OCCURRENCE_ID, Occurrence::getOccurrenceId)
            .returns(DATE, Occurrence::getDate)
            .returns(TIME, Occurrence::getTime)
            .returns(OccurrenceStatus.EXPIRED, Occurrence::getStatus)
            .returns(NOTE, Occurrence::getNote)
            .returns(REMIND_ME_BEFORE_DAYS, Occurrence::getRemindMeBeforeDays)
            .returns(false, Occurrence::isReminded);

        assertThat(entity.getStatus()).isEqualTo(ENCRYPTED_NEW_STATUS);
        then(occurrenceRepository).should().save(entity);
    }
}
