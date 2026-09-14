package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
    private static final String NOTE = "note";
    private static final Integer REMIND_ME_BEFORE_DAYS = 3;
    private static final String DATE_STRING = "date";
    private static final String TIME_STRING = "time";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private DateTimeConverter dateTimeConverter;

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
            .autoDone(true)
            .build();

        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(uuidConverter.convertDomain(OCCURRENCE_ID)).willReturn(OCCURRENCE_ID_STRING);
        given(dateTimeConverter.convertDomain(DATE)).willReturn(DATE_STRING);
        given(dateTimeConverter.convertDomain(TIME)).willReturn(TIME_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(USER_ID_STRING, OccurrenceEntity::getUserId)
            .returns(EVENT_ID_STRING, OccurrenceEntity::getEventId)
            .returns(OCCURRENCE_ID_STRING, OccurrenceEntity::getOccurrenceId)
            .returns(DATE.getYear() + "-" + DATE.getMonthValue(), OccurrenceEntity::getDateBucket)
            .returns(DATE_STRING, OccurrenceEntity::getDate)
            .returns(TIME_STRING, OccurrenceEntity::getTime)
            .returns(OccurrenceStatus.DONE.name(), OccurrenceEntity::getStatus)
            .returns(NOTE, OccurrenceEntity::getNote)
            .returns(String.valueOf(REMIND_ME_BEFORE_DAYS), OccurrenceEntity::getRemindMeBeforeDays)
            .returns(String.valueOf(true), OccurrenceEntity::getReminded)
            .returns(String.valueOf(true) , OccurrenceEntity::getAutoDone);
    }

    @Test
    void convertEntity() {
        OccurrenceEntity entity = OccurrenceEntity.builder()
            .userId(USER_ID_STRING)
            .eventId(EVENT_ID_STRING)
            .occurrenceId(OCCURRENCE_ID_STRING)
            .date(DATE_STRING)
            .time(TIME_STRING)
            .status(OccurrenceStatus.PENDING.name())
            .note(NOTE)
            .remindMeBeforeDays(String.valueOf(REMIND_ME_BEFORE_DAYS))
            .reminded(String.valueOf(false))
            .autoDone(String.valueOf(true))
            .build();

        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(OCCURRENCE_ID_STRING)).willReturn(OCCURRENCE_ID);
        given(dateTimeUtil.getCurrentDate()).willReturn(DATE.minusDays(1));
        given(dateTimeConverter.convertToLocalDate(DATE_STRING)).willReturn(DATE);
        given(dateTimeConverter.convertToLocalTime(TIME_STRING)).willReturn(TIME);

        assertThat(underTest.convertEntity(entity))
            .returns(USER_ID, Occurrence::getUserId)
            .returns(EVENT_ID, Occurrence::getEventId)
            .returns(OCCURRENCE_ID, Occurrence::getOccurrenceId)
            .returns(DATE, Occurrence::getDate)
            .returns(TIME, Occurrence::getTime)
            .returns(OccurrenceStatus.PENDING, Occurrence::getStatus)
            .returns(NOTE, Occurrence::getNote)
            .returns(REMIND_ME_BEFORE_DAYS, Occurrence::getRemindMeBeforeDays)
            .returns(false, Occurrence::isReminded)
            .returns(true, Occurrence::getAutoDone);
    }

    @Test
    void convertEntity_statusExpired() {
        OccurrenceEntity entity = OccurrenceEntity.builder()
            .userId(USER_ID_STRING)
            .eventId(EVENT_ID_STRING)
            .occurrenceId(OCCURRENCE_ID_STRING)
            .date(DATE_STRING)
            .time(TIME_STRING)
            .status(OccurrenceStatus.PENDING.name())
            .note(NOTE)
            .remindMeBeforeDays(String.valueOf(REMIND_ME_BEFORE_DAYS))
            .reminded(String.valueOf(false))
            .autoDone(String.valueOf(true))
            .build();

        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(OCCURRENCE_ID_STRING)).willReturn(OCCURRENCE_ID);
        given(dateTimeUtil.getCurrentDate()).willReturn(DATE.plusDays(1));
        given(dateTimeConverter.convertToLocalDate(DATE_STRING)).willReturn(DATE);
        given(dateTimeConverter.convertToLocalTime(TIME_STRING)).willReturn(TIME);

        assertThat(underTest.convertEntity(entity))
            .returns(USER_ID, Occurrence::getUserId)
            .returns(EVENT_ID, Occurrence::getEventId)
            .returns(OCCURRENCE_ID, Occurrence::getOccurrenceId)
            .returns(DATE, Occurrence::getDate)
            .returns(TIME, Occurrence::getTime)
            .returns(OccurrenceStatus.EXPIRED, Occurrence::getStatus)
            .returns(NOTE, Occurrence::getNote)
            .returns(REMIND_ME_BEFORE_DAYS, Occurrence::getRemindMeBeforeDays)
            .returns(false, Occurrence::isReminded)
            .returns(true, Occurrence::getAutoDone);

        assertThat(entity.getStatus()).isEqualTo(OccurrenceStatus.EXPIRED.name());
        then(occurrenceRepository).should().save(entity);
    }
}