package com.github.saphyra.apphub.service.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
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
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalTime TIME = LocalTime.now();
    private static final Integer REMIND_ME_BEFORE_DAYS = 3;
    private static final String NOTE = "note";
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private OccurrenceFactory underTest;

    @Test
    void create_pending() {
        given(idGenerator.randomUuid()).willReturn(OCCURRENCE_ID);
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);

        assertThat(underTest.create(USER_ID, EVENT_ID, CURRENT_DATE, TIME, REMIND_ME_BEFORE_DAYS, NOTE))
            .returns(OCCURRENCE_ID, Occurrence::getOccurrenceId)
            .returns(USER_ID, Occurrence::getUserId)
            .returns(EVENT_ID, Occurrence::getEventId)
            .returns(OccurrenceStatus.PENDING, Occurrence::getStatus)
            .returns(CURRENT_DATE, Occurrence::getDate)
            .returns(TIME, Occurrence::getTime)
            .returns(NOTE, Occurrence::getNote)
            .returns(REMIND_ME_BEFORE_DAYS, Occurrence::getRemindMeBeforeDays)
            .returns(false, Occurrence::getReminded);
    }

    @Test
    void create_expired() {
        given(idGenerator.randomUuid()).willReturn(OCCURRENCE_ID);
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);

        assertThat(underTest.create(USER_ID, EVENT_ID, CURRENT_DATE.minusDays(1), TIME, REMIND_ME_BEFORE_DAYS, NOTE))
            .returns(OCCURRENCE_ID, Occurrence::getOccurrenceId)
            .returns(USER_ID, Occurrence::getUserId)
            .returns(EVENT_ID, Occurrence::getEventId)
            .returns(OccurrenceStatus.EXPIRED, Occurrence::getStatus)
            .returns(CURRENT_DATE.minusDays(1), Occurrence::getDate)
            .returns(TIME, Occurrence::getTime)
            .returns(NOTE, Occurrence::getNote)
            .returns(REMIND_ME_BEFORE_DAYS, Occurrence::getRemindMeBeforeDays)
            .returns(false, Occurrence::getReminded);
    }
}