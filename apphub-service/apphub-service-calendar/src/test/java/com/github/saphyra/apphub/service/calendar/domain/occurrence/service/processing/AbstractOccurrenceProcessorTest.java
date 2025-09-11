package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.processing;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceFactory;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition.RepetitionTypeCondition;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition.RepetitionTypeConditionSelector;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AbstractOccurrenceProcessorTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final String REPETITION_DATA = "repetition-data";
    private static final LocalDate START_DATE = LocalDate.now().minusDays(5);
    private static final LocalDate END_DATE = LocalDate.now().plusDays(10);
    private static final Integer REPEAT_FOR_DAYS = 5;
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalDate OCCURRENCE_DATE = LocalDate.now().plusDays(1);
    private static final LocalDate EXISTING_DONE_OCCURRENCE_DATE = CURRENT_DATE.minusDays(1);
    private static final LocalDate EXISTING_EXPIRED_OCCURRENCE_DATE = CURRENT_DATE.minusDays(2);
    private static final LocalDate EXISTING_FUTURE_OCCURRENCE_DATE = CURRENT_DATE.plusDays(2);
    private static final LocalDate EXISTING_OCCURRENCE_NOT_ON_DATE = CURRENT_DATE.minusDays(3);

    @Mock
    private OccurrenceFactory occurrenceFactory;

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private RepetitionTypeConditionSelector repetitionTypeConditionSelector;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private Function<EventRequest, LocalDate> requestToEndDateMapper;

    @Mock
    private Function<Event, LocalDate> eventToEndDateMapper;

    private TestOccurrenceProcessor underTest;

    @Mock
    private EventRequest eventRequest;

    @Mock
    private RepetitionTypeCondition repetitionTypeCondition;

    @Mock
    private Occurrence occurrence;

    @Mock
    private UpdateEventContext updateEventContext;

    @Mock
    private Occurrence existingExpiredOccurrence;

    @Mock
    private Occurrence existingDoneOccurrence;

    @Mock
    private Occurrence existingFutureOccurrence;

    @Mock
    private Occurrence existingOccurrenceNotOnDate;

    @Mock
    private Occurrence existingExpiredOccurrenceNotOnDate;

    @Mock
    private Event event;

    @Captor
    private ArgumentCaptor<Predicate<Occurrence>> predicateArgumentCaptor;

    @BeforeEach
    void setUp(){
        underTest = TestOccurrenceProcessor.builder()
            .occurrenceFactory(occurrenceFactory)
            .occurrenceDao(occurrenceDao)
            .repetitionTypeConditionSelector(repetitionTypeConditionSelector)
            .dateTimeUtil(dateTimeUtil)
            .requestToEndDateMapper(requestToEndDateMapper)
            .eventToEndDateMapper(eventToEndDateMapper)
            .build();
    }

    @Test
    void createOccurrences_emptyDateList() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(eventRequest.getRepetitionData()).willReturn(REPETITION_DATA);
        given(repetitionTypeConditionSelector.get(RepetitionType.EVERY_X_DAYS, REPETITION_DATA)).willReturn(repetitionTypeCondition);
        given(repetitionTypeCondition.getOccurrences(START_DATE, END_DATE, REPEAT_FOR_DAYS, CURRENT_DATE)).willReturn(List.of());
        given(eventRequest.getStartDate()).willReturn(START_DATE);
        given(requestToEndDateMapper.apply(eventRequest)).willReturn(END_DATE);
        given(eventRequest.getRepeatForDays()).willReturn(REPEAT_FOR_DAYS);

        assertThat(catchThrowable(() -> underTest.createOccurrences(USER_ID, EVENT_ID, eventRequest)))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void createOccurrences() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(eventRequest.getRepetitionData()).willReturn(REPETITION_DATA);
        given(repetitionTypeConditionSelector.get(RepetitionType.EVERY_X_DAYS, REPETITION_DATA)).willReturn(repetitionTypeCondition);
        given(repetitionTypeCondition.getOccurrences(START_DATE, END_DATE, REPEAT_FOR_DAYS, CURRENT_DATE)).willReturn(List.of(OCCURRENCE_DATE));
        given(eventRequest.getStartDate()).willReturn(START_DATE);
        given(requestToEndDateMapper.apply(eventRequest)).willReturn(END_DATE);
        given(eventRequest.getRepeatForDays()).willReturn(REPEAT_FOR_DAYS);
        given(occurrenceFactory.create(USER_ID, EVENT_ID, OCCURRENCE_DATE, null, null)).willReturn(occurrence);

        underTest.createOccurrences(USER_ID, EVENT_ID, eventRequest);

        then(occurrenceDao).should().save(occurrence);
    }

    @Test
    void recreateOccurrences_emptyDateList() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(updateEventContext.getOccurrences()).willReturn(List.of());
        given(updateEventContext.getEvent()).willReturn(event);
        given(event.getRepetitionData()).willReturn(REPETITION_DATA);
        given(repetitionTypeConditionSelector.get(RepetitionType.EVERY_X_DAYS, REPETITION_DATA)).willReturn(repetitionTypeCondition);
        given(event.getStartDate()).willReturn(START_DATE);
        given(eventToEndDateMapper.apply(event)).willReturn(END_DATE);
        given(event.getRepeatForDays()).willReturn(REPEAT_FOR_DAYS);
        given(repetitionTypeCondition.getOccurrences(START_DATE, END_DATE, REPEAT_FOR_DAYS, CURRENT_DATE)).willReturn(List.of());

        assertThat(catchThrowable(() -> underTest.recreateOccurrences(updateEventContext)))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void recreateOccurrences() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        List<Occurrence> existingOccurrences = List.of(existingDoneOccurrence, existingExpiredOccurrence, existingFutureOccurrence, existingOccurrenceNotOnDate, existingExpiredOccurrenceNotOnDate);
        given(updateEventContext.getOccurrences()).willReturn(existingOccurrences);
        given(updateEventContext.getEvent()).willReturn(event);

        given(existingDoneOccurrence.getDate()).willReturn(EXISTING_DONE_OCCURRENCE_DATE);
        given(existingExpiredOccurrence.getDate()).willReturn(EXISTING_EXPIRED_OCCURRENCE_DATE);
        given(existingFutureOccurrence.getDate()).willReturn(EXISTING_FUTURE_OCCURRENCE_DATE);
        given(existingOccurrenceNotOnDate.getDate()).willReturn(EXISTING_OCCURRENCE_NOT_ON_DATE);
        given(existingExpiredOccurrenceNotOnDate.getDate()).willReturn(EXISTING_OCCURRENCE_NOT_ON_DATE);

        given(existingDoneOccurrence.getStatus()).willReturn(OccurrenceStatus.DONE);
        given(existingExpiredOccurrence.getStatus()).willReturn(OccurrenceStatus.EXPIRED);
        given(existingFutureOccurrence.getStatus()).willReturn(OccurrenceStatus.PENDING);
        given(existingOccurrenceNotOnDate.getStatus()).willReturn(OccurrenceStatus.EXPIRED);
        given(existingExpiredOccurrenceNotOnDate.getStatus()).willReturn(OccurrenceStatus.EXPIRED);

        given(event.getRepetitionData()).willReturn(REPETITION_DATA);
        given(repetitionTypeConditionSelector.get(RepetitionType.EVERY_X_DAYS, REPETITION_DATA)).willReturn(repetitionTypeCondition);
        given(event.getStartDate()).willReturn(START_DATE);
        given(eventToEndDateMapper.apply(event)).willReturn(END_DATE);
        given(event.getRepeatForDays()).willReturn(REPEAT_FOR_DAYS);
        given(repetitionTypeCondition.getOccurrences(START_DATE, END_DATE, REPEAT_FOR_DAYS, CURRENT_DATE))
            .willReturn(List.of(OCCURRENCE_DATE, EXISTING_DONE_OCCURRENCE_DATE, EXISTING_EXPIRED_OCCURRENCE_DATE, EXISTING_FUTURE_OCCURRENCE_DATE));

        given(event.getUserId()).willReturn(USER_ID);
        given(event.getEventId()).willReturn(EVENT_ID);
        given(occurrenceFactory.create(USER_ID, EVENT_ID, OCCURRENCE_DATE, null, null)).willReturn(occurrence);

        underTest.recreateOccurrences(updateEventContext);

        then(updateEventContext).should().deleteOccurrences(predicateArgumentCaptor.capture());
        assertThat(predicateArgumentCaptor.getValue().test(existingDoneOccurrence)).isFalse();
        assertThat(predicateArgumentCaptor.getValue().test(existingExpiredOccurrence)).isFalse();
        assertThat(predicateArgumentCaptor.getValue().test(existingFutureOccurrence)).isFalse();
        assertThat(predicateArgumentCaptor.getValue().test(existingOccurrenceNotOnDate)).isTrue();
        assertThat(predicateArgumentCaptor.getValue().test(existingExpiredOccurrenceNotOnDate)).isTrue();

        ArgumentCaptor<Occurrence> argumentCaptor = ArgumentCaptor.forClass(Occurrence.class);
        then(updateEventContext).should().addOccurrence(argumentCaptor.capture());
        assertThat(argumentCaptor.getAllValues()).containsExactly(occurrence);
    }

    static class TestOccurrenceProcessor extends AbstractOccurrenceProcessor {
        private final Function<EventRequest, LocalDate> requestToEndDateMapper;
        private final Function<Event, LocalDate> eventToEndDateMapper;

        @Builder
        TestOccurrenceProcessor(
            OccurrenceFactory occurrenceFactory,
            OccurrenceDao occurrenceDao,
            RepetitionTypeConditionSelector repetitionTypeConditionSelector,
            DateTimeUtil dateTimeUtil,
            Function<EventRequest, LocalDate> requestToEndDateMapper,
            Function<Event, LocalDate> eventToEndDateMapper
        ) {
            super(occurrenceFactory, occurrenceDao, repetitionTypeConditionSelector, dateTimeUtil);
            this.requestToEndDateMapper = requestToEndDateMapper;
            this.eventToEndDateMapper = eventToEndDateMapper;
        }

        @Override
        public RepetitionType getRepetitionType() {
            return RepetitionType.EVERY_X_DAYS;
        }

        @Override
        protected LocalDate getEndDate(EventRequest request) {
            return requestToEndDateMapper.apply(request);
        }

        @Override
        protected LocalDate getEndDate(Event event) {
            return eventToEndDateMapper.apply(event);
        }
    }
}