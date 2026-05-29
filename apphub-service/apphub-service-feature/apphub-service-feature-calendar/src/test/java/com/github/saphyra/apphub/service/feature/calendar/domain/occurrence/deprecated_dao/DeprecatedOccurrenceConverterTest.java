package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@Deprecated(forRemoval = true)
class DeprecatedOccurrenceConverterTest {
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalTime TIME = LocalTime.now();
    private static final String NOTE = "note";
    private static final Integer REMIND_ME_BEFORE_DAYS = 42;
    private static final String USER_ID_FROM_ACCESS_TOKEN = "user-id-from-access-token";
    private static final String OCCURRENCE_ID_STRING = "occurrence-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String EVENT_ID_STRING = "event-id";
    private static final String ENCRYPTED_DATE = "encrypted-date";
    private static final String ENCRYPTED_TIME = "encrypted-time";
    private static final String ENCRYPTED_STATUS = "encrypted-status";
    private static final String ENCRYPTED_NOTE = "encrypted-note";
    private static final String ENCRYPTED_REMIND_ME_BEFORE_DAYS = "encrypted-remind-me-before-days";
    private static final String ENCRYPTED_REMINDED = "encrypted-reminded";

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private LocalDateEncryptor localDateEncryptor;

    @Mock
    private LocalTimeEncryptor localTimeEncryptor;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private BooleanEncryptor booleanEncryptor;

    @Mock
    private IntegerEncryptor integerEncryptor;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private DeprecatedOccurrenceRepository occurrenceRepository;

    @InjectMocks
    private DeprecatedOccurrenceConverter underTest;

    @Test
    void convertDomain() {
        DeprecatedOccurrence domain = DeprecatedOccurrence.builder()
            .occurrenceId(OCCURRENCE_ID)
            .userId(USER_ID)
            .eventId(EVENT_ID)
            .date(CURRENT_DATE)
            .time(TIME)
            .status(OccurrenceStatus.PENDING)
            .note(NOTE)
            .remindMeBeforeDays(REMIND_ME_BEFORE_DAYS)
            .reminded(true)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(uuidConverter.convertDomain(OCCURRENCE_ID)).willReturn(OCCURRENCE_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(localDateEncryptor.encrypt(CURRENT_DATE, USER_ID_FROM_ACCESS_TOKEN, OCCURRENCE_ID_STRING, DeprecatedOccurrenceConverter.COLUMN_DATE)).willReturn(ENCRYPTED_DATE);
        given(localTimeEncryptor.encrypt(TIME, USER_ID_FROM_ACCESS_TOKEN, OCCURRENCE_ID_STRING, DeprecatedOccurrenceConverter.COLUMN_TIME)).willReturn(ENCRYPTED_TIME);
        given(stringEncryptor.encrypt(OccurrenceStatus.PENDING.name(), USER_ID_FROM_ACCESS_TOKEN, OCCURRENCE_ID_STRING, DeprecatedOccurrenceConverter.COLUMN_STATUS)).willReturn(ENCRYPTED_STATUS);
        given(stringEncryptor.encrypt(NOTE, USER_ID_FROM_ACCESS_TOKEN, OCCURRENCE_ID_STRING, DeprecatedOccurrenceConverter.COLUMN_NOTE)).willReturn(ENCRYPTED_NOTE);
        given(integerEncryptor.encrypt(REMIND_ME_BEFORE_DAYS, USER_ID_FROM_ACCESS_TOKEN, OCCURRENCE_ID_STRING, DeprecatedOccurrenceConverter.COLUMN_REMIND_ME_BEFORE_DAYS)).willReturn(ENCRYPTED_REMIND_ME_BEFORE_DAYS);
        given(booleanEncryptor.encrypt(true, USER_ID_FROM_ACCESS_TOKEN, OCCURRENCE_ID_STRING, DeprecatedOccurrenceConverter.COLUMN_REMINDED)).willReturn(ENCRYPTED_REMINDED);

        assertThat(underTest.convertDomain(domain))
            .returns(OCCURRENCE_ID_STRING, DeprecatedOccurrenceEntity::getOccurrenceId)
            .returns(USER_ID_STRING, DeprecatedOccurrenceEntity::getUserId)
            .returns(EVENT_ID_STRING, DeprecatedOccurrenceEntity::getEventId)
            .returns(ENCRYPTED_DATE, DeprecatedOccurrenceEntity::getDate)
            .returns(ENCRYPTED_TIME, DeprecatedOccurrenceEntity::getTime)
            .returns(ENCRYPTED_STATUS, DeprecatedOccurrenceEntity::getStatus)
            .returns(ENCRYPTED_NOTE, DeprecatedOccurrenceEntity::getNote)
            .returns(ENCRYPTED_REMIND_ME_BEFORE_DAYS, DeprecatedOccurrenceEntity::getRemindMeBeforeDays)
            .returns(ENCRYPTED_REMINDED, DeprecatedOccurrenceEntity::getReminded);
    }

    @Test
    void convertEntity() {
        DeprecatedOccurrenceEntity entity = DeprecatedOccurrenceEntity.builder()
            .occurrenceId(OCCURRENCE_ID_STRING)
            .userId(USER_ID_STRING)
            .eventId(EVENT_ID_STRING)
            .date(ENCRYPTED_DATE)
            .time(ENCRYPTED_TIME)
            .status(ENCRYPTED_STATUS)
            .note(ENCRYPTED_NOTE)
            .remindMeBeforeDays(ENCRYPTED_REMIND_ME_BEFORE_DAYS)
            .reminded(ENCRYPTED_REMINDED)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(uuidConverter.convertEntity(OCCURRENCE_ID_STRING)).willReturn(OCCURRENCE_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(localDateEncryptor.decrypt(ENCRYPTED_DATE, USER_ID_FROM_ACCESS_TOKEN, OCCURRENCE_ID_STRING, DeprecatedOccurrenceConverter.COLUMN_DATE)).willReturn(CURRENT_DATE);
        given(localTimeEncryptor.decrypt(ENCRYPTED_TIME, USER_ID_FROM_ACCESS_TOKEN, OCCURRENCE_ID_STRING, DeprecatedOccurrenceConverter.COLUMN_TIME)).willReturn(TIME);
        given(stringEncryptor.decrypt(ENCRYPTED_STATUS, USER_ID_FROM_ACCESS_TOKEN, OCCURRENCE_ID_STRING, DeprecatedOccurrenceConverter.COLUMN_STATUS)).willReturn(OccurrenceStatus.PENDING.name());
        given(stringEncryptor.decrypt(ENCRYPTED_NOTE, USER_ID_FROM_ACCESS_TOKEN, OCCURRENCE_ID_STRING, DeprecatedOccurrenceConverter.COLUMN_NOTE)).willReturn(NOTE);
        given(integerEncryptor.decrypt(ENCRYPTED_REMIND_ME_BEFORE_DAYS, USER_ID_FROM_ACCESS_TOKEN, OCCURRENCE_ID_STRING, DeprecatedOccurrenceConverter.COLUMN_REMIND_ME_BEFORE_DAYS)).willReturn(REMIND_ME_BEFORE_DAYS);
        given(booleanEncryptor.decrypt(ENCRYPTED_REMINDED, USER_ID_FROM_ACCESS_TOKEN, OCCURRENCE_ID_STRING, DeprecatedOccurrenceConverter.COLUMN_REMINDED)).willReturn(true);
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);

        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE.minusDays(1));

        assertThat(underTest.convertEntity(entity))
            .returns(OCCURRENCE_ID, DeprecatedOccurrence::getOccurrenceId)
            .returns(USER_ID, DeprecatedOccurrence::getUserId)
            .returns(EVENT_ID, DeprecatedOccurrence::getEventId)
            .returns(CURRENT_DATE, DeprecatedOccurrence::getDate)
            .returns(TIME, DeprecatedOccurrence::getTime)
            .returns(OccurrenceStatus.PENDING, DeprecatedOccurrence::getStatus)
            .returns(NOTE, DeprecatedOccurrence::getNote)
            .returns(REMIND_ME_BEFORE_DAYS, DeprecatedOccurrence::getRemindMeBeforeDays)
            .returns(true, DeprecatedOccurrence::getReminded);
    }

    @Test
    void convertEntity_pendingToExpired() {
        DeprecatedOccurrenceEntity entity = DeprecatedOccurrenceEntity.builder()
            .occurrenceId(OCCURRENCE_ID_STRING)
            .userId(USER_ID_STRING)
            .eventId(EVENT_ID_STRING)
            .date(ENCRYPTED_DATE)
            .time(ENCRYPTED_TIME)
            .status(ENCRYPTED_STATUS)
            .note(ENCRYPTED_NOTE)
            .remindMeBeforeDays(ENCRYPTED_REMIND_ME_BEFORE_DAYS)
            .reminded(ENCRYPTED_REMINDED)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(uuidConverter.convertEntity(OCCURRENCE_ID_STRING)).willReturn(OCCURRENCE_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(localDateEncryptor.decrypt(ENCRYPTED_DATE, USER_ID_FROM_ACCESS_TOKEN, OCCURRENCE_ID_STRING, DeprecatedOccurrenceConverter.COLUMN_DATE)).willReturn(CURRENT_DATE.minusDays(1));
        given(localTimeEncryptor.decrypt(ENCRYPTED_TIME, USER_ID_FROM_ACCESS_TOKEN, OCCURRENCE_ID_STRING, DeprecatedOccurrenceConverter.COLUMN_TIME)).willReturn(TIME);
        given(stringEncryptor.decrypt(ENCRYPTED_STATUS, USER_ID_FROM_ACCESS_TOKEN, OCCURRENCE_ID_STRING, DeprecatedOccurrenceConverter.COLUMN_STATUS)).willReturn(OccurrenceStatus.PENDING.name());
        given(stringEncryptor.decrypt(ENCRYPTED_NOTE, USER_ID_FROM_ACCESS_TOKEN, OCCURRENCE_ID_STRING, DeprecatedOccurrenceConverter.COLUMN_NOTE)).willReturn(NOTE);
        given(integerEncryptor.decrypt(ENCRYPTED_REMIND_ME_BEFORE_DAYS, USER_ID_FROM_ACCESS_TOKEN, OCCURRENCE_ID_STRING, DeprecatedOccurrenceConverter.COLUMN_REMIND_ME_BEFORE_DAYS)).willReturn(REMIND_ME_BEFORE_DAYS);
        given(booleanEncryptor.decrypt(ENCRYPTED_REMINDED, USER_ID_FROM_ACCESS_TOKEN, OCCURRENCE_ID_STRING, DeprecatedOccurrenceConverter.COLUMN_REMINDED)).willReturn(true);
        given(stringEncryptor.encrypt(OccurrenceStatus.EXPIRED.name(), USER_ID_FROM_ACCESS_TOKEN, OCCURRENCE_ID_STRING, DeprecatedOccurrenceConverter.COLUMN_STATUS)).willReturn(ENCRYPTED_STATUS);
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);

        assertThat(underTest.convertEntity(entity))
            .returns(OCCURRENCE_ID, DeprecatedOccurrence::getOccurrenceId)
            .returns(USER_ID, DeprecatedOccurrence::getUserId)
            .returns(EVENT_ID, DeprecatedOccurrence::getEventId)
            .returns(CURRENT_DATE.minusDays(1), DeprecatedOccurrence::getDate)
            .returns(TIME, DeprecatedOccurrence::getTime)
            .returns(OccurrenceStatus.EXPIRED, DeprecatedOccurrence::getStatus)
            .returns(NOTE, DeprecatedOccurrence::getNote)
            .returns(REMIND_ME_BEFORE_DAYS, DeprecatedOccurrence::getRemindMeBeforeDays)
            .returns(true, DeprecatedOccurrence::getReminded);

        assertThat(entity.getStatus()).isEqualTo(ENCRYPTED_STATUS);
        then(occurrenceRepository).should().save(entity);
    }
}