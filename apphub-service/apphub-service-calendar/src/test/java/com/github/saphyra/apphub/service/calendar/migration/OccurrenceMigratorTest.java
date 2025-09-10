package com.github.saphyra.apphub.service.calendar.migration;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition.RepetitionTypeCondition;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition.RepetitionTypeConditionSelector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OccurrenceMigratorTest {
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final LocalDate PAST_DATE = CURRENT_DATE.minusDays(1);
    private static final String REPETITION_DATA = "repetition-data";
    private static final LocalDate START_DATE = LocalDate.now().minusDays(10);
    private static final LocalDate END_DATE = LocalDate.now().plusDays(10);
    private static final Integer REPEAT_FOR_DAYS = 5;
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String NOTE = "note";
    private static final LocalDate FUTURE_DATE = CURRENT_DATE.plusDays(1);

    @Mock
    private MigrationDao migrationDao;

    @Mock
    private RepetitionTypeConditionSelector repetitionTypeConditionSelector;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private OccurrenceMigrator underTest;

    @Mock
    private Event event;

    @Mock
    private DeprecatedOccurrence deprecatedOccurrence;

    @Mock
    private RepetitionTypeCondition repetitionTypeCondition;

    @BeforeEach
    void setUp() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(event.getEventId()).willReturn(EVENT_ID);
        given(migrationDao.getOccurrences(EVENT_ID)).willReturn(List.of(deprecatedOccurrence));
        given(event.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);
        given(event.getRepetitionData()).willReturn(REPETITION_DATA);
        given(event.getStartDate()).willReturn(START_DATE);
        given(event.getEndDate()).willReturn(END_DATE);
        given(event.getRepeatForDays()).willReturn(REPEAT_FOR_DAYS);
        given(repetitionTypeConditionSelector.get(RepetitionType.EVERY_X_DAYS, REPETITION_DATA)).willReturn(repetitionTypeCondition);
    }

    @Test
    void pastDateWithExistingOccurrence() {
        given(repetitionTypeCondition.getOccurrences(START_DATE, END_DATE, REPEAT_FOR_DAYS, null)).willReturn(List.of(PAST_DATE));

        given(deprecatedOccurrence.getOccurrenceId()).willReturn(OCCURRENCE_ID);
        given(deprecatedOccurrence.getUserId()).willReturn(USER_ID);
        given(deprecatedOccurrence.getEventId()).willReturn(EVENT_ID);
        given(deprecatedOccurrence.getDate()).willReturn(PAST_DATE);
        given(deprecatedOccurrence.getStatus()).willReturn(OccurrenceStatus.DONE);
        given(deprecatedOccurrence.getNote()).willReturn(NOTE);

        underTest.migrate(event);

        ArgumentCaptor<Occurrence> argumentCaptor = ArgumentCaptor.forClass(Occurrence.class);
        then(occurrenceDao).should().save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue())
            .returns(OCCURRENCE_ID, Occurrence::getOccurrenceId)
            .returns(USER_ID, Occurrence::getUserId)
            .returns(EVENT_ID, Occurrence::getEventId)
            .returns(PAST_DATE, Occurrence::getDate)
            .returns(OccurrenceStatus.DONE, Occurrence::getStatus)
            .returns(NOTE, Occurrence::getNote);
    }

    @Test
    void pastDateWithExistingPendingOccurrence() {
        given(repetitionTypeCondition.getOccurrences(START_DATE, END_DATE, REPEAT_FOR_DAYS, null)).willReturn(List.of(PAST_DATE));

        given(deprecatedOccurrence.getDate()).willReturn(PAST_DATE);
        given(deprecatedOccurrence.getStatus()).willReturn(OccurrenceStatus.PENDING);

        underTest.migrate(event);

        then(occurrenceDao).shouldHaveNoInteractions();
    }

    @Test
    void pastDateWithNoOccurrence() {
        given(repetitionTypeCondition.getOccurrences(START_DATE, END_DATE, REPEAT_FOR_DAYS, null)).willReturn(List.of(PAST_DATE));

        given(deprecatedOccurrence.getDate()).willReturn(PAST_DATE.minusDays(1));

        underTest.migrate(event);

        then(occurrenceDao).shouldHaveNoInteractions();
    }

    @Test
    void futureDateWithExistingOccurrence() {
        given(repetitionTypeCondition.getOccurrences(START_DATE, END_DATE, REPEAT_FOR_DAYS, null)).willReturn(List.of(FUTURE_DATE));

        given(deprecatedOccurrence.getOccurrenceId()).willReturn(OCCURRENCE_ID);
        given(deprecatedOccurrence.getUserId()).willReturn(USER_ID);
        given(deprecatedOccurrence.getEventId()).willReturn(EVENT_ID);
        given(deprecatedOccurrence.getDate()).willReturn(FUTURE_DATE);
        given(deprecatedOccurrence.getStatus()).willReturn(OccurrenceStatus.DONE);
        given(deprecatedOccurrence.getNote()).willReturn(NOTE);

        underTest.migrate(event);

        ArgumentCaptor<Occurrence> argumentCaptor = ArgumentCaptor.forClass(Occurrence.class);
        then(occurrenceDao).should().save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue())
            .returns(OCCURRENCE_ID, Occurrence::getOccurrenceId)
            .returns(USER_ID, Occurrence::getUserId)
            .returns(EVENT_ID, Occurrence::getEventId)
            .returns(FUTURE_DATE, Occurrence::getDate)
            .returns(OccurrenceStatus.DONE, Occurrence::getStatus)
            .returns(NOTE, Occurrence::getNote);
    }

    @Test
    void futureDateWithNoOccurrence() {
        given(repetitionTypeCondition.getOccurrences(START_DATE, END_DATE, REPEAT_FOR_DAYS, null)).willReturn(List.of(FUTURE_DATE));

        given(deprecatedOccurrence.getDate()).willReturn(PAST_DATE);
        given(idGenerator.randomUuid()).willReturn(OCCURRENCE_ID);
        given(event.getUserId()).willReturn(USER_ID);
        given(event.getEventId()).willReturn(EVENT_ID);

        underTest.migrate(event);

        ArgumentCaptor<Occurrence> argumentCaptor = ArgumentCaptor.forClass(Occurrence.class);
        then(occurrenceDao).should().save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue())
            .returns(OCCURRENCE_ID, Occurrence::getOccurrenceId)
            .returns(USER_ID, Occurrence::getUserId)
            .returns(EVENT_ID, Occurrence::getEventId)
            .returns(FUTURE_DATE, Occurrence::getDate)
            .returns(OccurrenceStatus.PENDING, Occurrence::getStatus)
            .returns("", Occurrence::getNote);
    }
}