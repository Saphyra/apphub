package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.calendar.config.CalendarParams;
import com.github.saphyra.apphub.service.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EventRequestValidatorTest {
    private static final UUID LABEL_ID = UUID.randomUUID();

    @Spy
    private final ObjectMapperWrapper objectMapperWrapper = new ObjectMapperWrapper(new ObjectMapper());

    @Mock
    private CalendarParams calendarParams;

    @Mock
    private LabelDao labelDao;

    @InjectMocks
    private EventRequestValidator underTest;

    @Mock
    private EventRequest request;

    @Test
    void nullRepetitionType() {
        given(request.getRepetitionType()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "repetitionType", "must not be null");
    }

    @Test
    void oneTimeEvent_nullRepeatForDays() {
        given(request.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);
        given(request.getRepeatForDays()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "repeatForDays", "must not be null");
    }

    @Test
    void oneTimeEvent_repeatForDaysTooLow() {
        given(request.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);
        given(request.getRepeatForDays()).willReturn(0);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "repeatForDays", "too low");
    }

    @Test
    void oneTimeEvent_nullStartDate() {
        given(request.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);
        given(request.getRepeatForDays()).willReturn(1);
        given(request.getStartDate()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "startDate", "must not be null");
    }

    @Test
    void oneTimeEvent_blankTitle() {
        given(request.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);
        given(request.getRepeatForDays()).willReturn(1);
        given(request.getStartDate()).willReturn(LocalDate.now());
        given(request.getTitle()).willReturn(" ");

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "title", "must not be null or blank");
    }

    @Test
    void oneTimeEvent_nullContent() {
        given(request.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);
        given(request.getRepeatForDays()).willReturn(1);
        given(request.getStartDate()).willReturn(LocalDate.now());
        given(request.getTitle()).willReturn("title");
        given(request.getContent()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "content", "must not be null");
    }

    @Test
    void oneTimeEvent_nullRemindMeBeforeDays() {
        given(request.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);
        given(request.getRepeatForDays()).willReturn(1);
        given(request.getStartDate()).willReturn(LocalDate.now());
        given(request.getTitle()).willReturn("title");
        given(request.getContent()).willReturn("content");
        given(request.getRemindMeBeforeDays()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "remindMeBeforeDays", "must not be null");
    }

    @Test
    void oneTimeEvent_negativeRemindMeBeforeDays() {
        given(request.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);
        given(request.getRepeatForDays()).willReturn(1);
        given(request.getStartDate()).willReturn(LocalDate.now());
        given(request.getTitle()).willReturn("title");
        given(request.getContent()).willReturn("content");
        given(request.getRemindMeBeforeDays()).willReturn(-1);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "remindMeBeforeDays", "too low");
    }

    @Test
    void oneTimeEvent_nullLabels() {
        given(request.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);
        given(request.getRepeatForDays()).willReturn(1);
        given(request.getStartDate()).willReturn(LocalDate.now());
        given(request.getTitle()).willReturn("title");
        given(request.getContent()).willReturn("content");
        given(request.getRemindMeBeforeDays()).willReturn(0);
        given(request.getLabels()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "labels", "must not be null");
    }

    @Test
    void oneTimeEvent_labelsContainsNull() {
        given(request.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);
        given(request.getRepeatForDays()).willReturn(1);
        given(request.getStartDate()).willReturn(LocalDate.now());
        given(request.getTitle()).willReturn("title");
        given(request.getContent()).willReturn("content");
        given(request.getRemindMeBeforeDays()).willReturn(0);
        given(request.getLabels()).willReturn(CollectionUtils.toList(LABEL_ID, null));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "labels", "must not contain null values");
    }

    @Test
    void oneTimeEvent_labelDoesNotExist() {
        given(request.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);
        given(request.getRepeatForDays()).willReturn(1);
        given(request.getStartDate()).willReturn(LocalDate.now());
        given(request.getTitle()).willReturn("title");
        given(request.getContent()).willReturn("content");
        given(request.getRemindMeBeforeDays()).willReturn(0);
        given(request.getLabels()).willReturn(CollectionUtils.toList(LABEL_ID));
        given(labelDao.existsById(LABEL_ID)).willReturn(false);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "labelId", "does not exist");
    }

    @Test
    void oneTimeEvent_valid() {
        given(request.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);
        given(request.getRepeatForDays()).willReturn(1);
        given(request.getStartDate()).willReturn(LocalDate.now());
        given(request.getTitle()).willReturn("title");
        given(request.getContent()).willReturn("content");
        given(request.getRemindMeBeforeDays()).willReturn(0);
        given(request.getLabels()).willReturn(CollectionUtils.toList(LABEL_ID));
        given(labelDao.existsById(LABEL_ID)).willReturn(true);

        underTest.validate(request);
    }

    @Test
    void everyXDaysEvent_nullRepetitionData() {
        given(request.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);
        given(request.getRepetitionData()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "repetitionData", "must not be null");
    }

    @Test
    void everyXDaysEvent_cannotParseRepetitionData() {
        given(request.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);
        given(request.getRepetitionData()).willReturn("not a number");

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "repetitionData", "failed to parse");
    }

    @Test
    void everyXDaysEvent_repetitionDataTooLow() {
        given(request.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);
        given(request.getRepetitionData()).willReturn(0);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "repetitionData", "too low");
    }

    @Test
    void everyXDaysEvent_nullEndDate() {
        given(request.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);
        given(request.getRepetitionData()).willReturn(1);
        given(request.getRepeatForDays()).willReturn(1);
        given(request.getStartDate()).willReturn(LocalDate.now());
        given(request.getEndDate()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "endDate", "must not be null");
    }

    @Test
    void everyXDaysEvent_endDateBeforeStartDate() {
        given(request.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);
        given(request.getRepetitionData()).willReturn(1);
        given(request.getRepeatForDays()).willReturn(1);
        given(request.getStartDate()).willReturn(LocalDate.now());
        given(request.getEndDate()).willReturn(LocalDate.now().minusDays(1));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "startDate", "startDate cannot be after endDate");
    }

    @Test
    void everyXDaysEvent_endDateTooFar() {
        given(request.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);
        given(request.getRepetitionData()).willReturn(1);
        given(request.getRepeatForDays()).willReturn(1);
        given(request.getStartDate()).willReturn(LocalDate.now());
        given(request.getEndDate()).willReturn(LocalDate.now().plusDays(31));
        given(calendarParams.getMaxEventDurationDays()).willReturn(30);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "eventDuration", "too long");
    }

    @Test
    void daysOfWeekEvent_nullRepetitionData() {
        given(request.getRepetitionType()).willReturn(RepetitionType.DAYS_OF_WEEK);
        given(request.getRepetitionData()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "repetitionData", "must not be null");
    }

    @Test
    void daysOfWeekEvent_cannotParseRepetitionData() {
        given(request.getRepetitionType()).willReturn(RepetitionType.DAYS_OF_WEEK);
        given(request.getRepetitionData()).willReturn("not a set of days");

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "repetitionData", "failed to parse");
    }

    @Test
    void daysOfWeekEvent_repetitionDataContainsNull() {
        given(request.getRepetitionType()).willReturn(RepetitionType.DAYS_OF_WEEK);
        given(request.getRepetitionData()).willReturn(CollectionUtils.toSet(DayOfWeek.MONDAY, null));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "repetitionData", "must not contain null values");
    }

    @Test
    void daysOfWeekEvent_repetitionDataEmpty() {
        given(request.getRepetitionType()).willReturn(RepetitionType.DAYS_OF_WEEK);
        given(request.getRepetitionData()).willReturn(List.of());

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "repetitionData", "must not be empty");
    }

    @Test
    void daysOfWeekEvent_valid() {
        given(request.getRepetitionType()).willReturn(RepetitionType.DAYS_OF_WEEK);
        given(request.getRepetitionData()).willReturn(CollectionUtils.toSet(DayOfWeek.MONDAY));
        given(request.getRepeatForDays()).willReturn(1);
        given(request.getStartDate()).willReturn(LocalDate.now());
        given(calendarParams.getMaxEventDurationDays()).willReturn(30);
        given(request.getEndDate()).willReturn(LocalDate.now().plusDays(1));
        given(request.getTitle()).willReturn("title");
        given(request.getContent()).willReturn("content");
        given(request.getRemindMeBeforeDays()).willReturn(0);
        given(request.getLabels()).willReturn(CollectionUtils.toList(LABEL_ID));
        given(labelDao.existsById(LABEL_ID)).willReturn(true);

        underTest.validate(request);
    }

    @Test
    void daysOfMonthEvent_nullRepetitionData() {
        given(request.getRepetitionType()).willReturn(RepetitionType.DAYS_OF_MONTH);
        given(request.getRepetitionData()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "repetitionData", "must not be null");
    }

    @Test
    void daysOfMonthEvent_cannotParseRepetitionData() {
        given(request.getRepetitionType()).willReturn(RepetitionType.DAYS_OF_MONTH);
        given(request.getRepetitionData()).willReturn("not a list of numbers");

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "repetitionData", "failed to parse");
    }

    @Test
    void daysOfMonthEvent_emptyRepetitionData() {
        given(request.getRepetitionType()).willReturn(RepetitionType.DAYS_OF_MONTH);
        given(request.getRepetitionData()).willReturn(List.of());

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "repetitionData", "must not be empty");
    }

    @Test
    void daysOfMonthEvent_repetitionDataContainsNull() {
        given(request.getRepetitionType()).willReturn(RepetitionType.DAYS_OF_MONTH);
        given(request.getRepetitionData()).willReturn(CollectionUtils.toList(1, null));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "repetitionData", "must not contain null values");
    }

    @Test
    void daysOfMonthEvent_repetitionDataTooLow() {
        given(request.getRepetitionType()).willReturn(RepetitionType.DAYS_OF_MONTH);
        given(request.getRepetitionData()).willReturn(CollectionUtils.toList(0));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "repetitionData", "too low");
    }

    @Test
    void daysOfMonthEvent_repetitionDataTooHigh() {
        given(request.getRepetitionType()).willReturn(RepetitionType.DAYS_OF_MONTH);
        given(request.getRepetitionData()).willReturn(CollectionUtils.toList(32));

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "repetitionData", "too high");
    }

    @Test
    void daysOfMonthEvent_valid() {
        given(request.getRepetitionType()).willReturn(RepetitionType.DAYS_OF_MONTH);
        given(request.getRepetitionData()).willReturn(CollectionUtils.toList(1, 31));
        given(request.getRepeatForDays()).willReturn(1);
        given(request.getStartDate()).willReturn(LocalDate.now());
        given(calendarParams.getMaxEventDurationDays()).willReturn(30);
        given(request.getEndDate()).willReturn(LocalDate.now().plusDays(1));
        given(request.getTitle()).willReturn("title");
        given(request.getContent()).willReturn("content");
        given(request.getRemindMeBeforeDays()).willReturn(0);
        given(request.getLabels()).willReturn(CollectionUtils.toList(LABEL_ID));
        given(labelDao.existsById(LABEL_ID)).willReturn(true);

        underTest.validate(request);
    }
}