package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
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
class OccurrenceFactoryTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final LocalDate CURRENT_DATE = LocalDate.of(2026, 6, 3);
    private static final LocalDate OCCURRENCE_DATE = CURRENT_DATE;
    private static final LocalDate EXPIRED_DATE = CURRENT_DATE.minusDays(1);
    private static final LocalTime TIME = LocalTime.of(12, 30);
    private static final Integer REMIND_ME_BEFORE_DAYS = 4;
    private static final String NOTE = "note";

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private OccurrenceFactory underTest;

    @Test
    void create_defaultsNoteAndCreatesPendingOccurrence() {
        given(idGenerator.randomUuid()).willReturn(OCCURRENCE_ID);
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);

        assertThat(underTest.create(USER_ID, EVENT_ID, OCCURRENCE_DATE, TIME, REMIND_ME_BEFORE_DAYS))
            .returns(USER_ID, Occurrence::getUserId)
            .returns(EVENT_ID, Occurrence::getEventId)
            .returns(OCCURRENCE_ID, Occurrence::getOccurrenceId)
            .returns(OccurrenceStatus.PENDING, Occurrence::getStatus)
            .returns(OCCURRENCE_DATE, Occurrence::getDate)
            .returns(TIME, Occurrence::getTime)
            .returns("", Occurrence::getNote)
            .returns(REMIND_ME_BEFORE_DAYS, Occurrence::getRemindMeBeforeDays)
            .returns(false, Occurrence::isReminded);
    }

    @Test
    void create_withNoteCreatesExpiredOccurrenceWhenDateIsInThePast() {
        given(idGenerator.randomUuid()).willReturn(OCCURRENCE_ID);
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);

        assertThat(underTest.create(USER_ID, EVENT_ID, EXPIRED_DATE, TIME, REMIND_ME_BEFORE_DAYS, NOTE, false))
            .returns(USER_ID, Occurrence::getUserId)
            .returns(EVENT_ID, Occurrence::getEventId)
            .returns(OCCURRENCE_ID, Occurrence::getOccurrenceId)
            .returns(OccurrenceStatus.EXPIRED, Occurrence::getStatus)
            .returns(EXPIRED_DATE, Occurrence::getDate)
            .returns(TIME, Occurrence::getTime)
            .returns(NOTE, Occurrence::getNote)
            .returns(REMIND_ME_BEFORE_DAYS, Occurrence::getRemindMeBeforeDays)
            .returns(false, Occurrence::isReminded);
    }
}
