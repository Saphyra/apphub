package com.github.saphyra.apphub.integration.backend.calendar.event;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CalendarEventValidationTest extends BackEndTest {
    @Test(groups = {"be", "calendar"})
    public void calendarEventValidation() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        oneTime(accessTokenId);
        everyXDays(accessTokenId);
        daysOfWeek(accessTokenId);
        daysOfMonth(accessTokenId);
    }

    private void daysOfMonth(UUID accessTokenId) {
        create_generic(accessTokenId, RepetitionType.DAYS_OF_MONTH);
        create_endDate(accessTokenId, RepetitionType.DAYS_OF_MONTH);
        create_daysOfMonth_repetitionData(accessTokenId);
        UUID eventId = create(accessTokenId, RepetitionType.DAYS_OF_MONTH);
        edit_generic(accessTokenId, eventId, RepetitionType.DAYS_OF_MONTH);
        edit_endDate(accessTokenId, eventId, RepetitionType.DAYS_OF_MONTH);
        edit_daysOfMonth_repetitionData(accessTokenId, eventId);
        edit(accessTokenId, eventId, RepetitionType.DAYS_OF_MONTH);
    }

    private void edit_daysOfMonth_repetitionData(UUID accessTokenId, UUID eventId) {
        edit_nullRepetitionData(accessTokenId, eventId, RepetitionType.DAYS_OF_MONTH);
        edit_invalidRepetitionData(accessTokenId, eventId, RepetitionType.DAYS_OF_MONTH);
        edit_daysOfMonth_repetitionDataEmpty(accessTokenId, eventId);
        edit_dayOfMonth_repetitionDataContainsNull(accessTokenId, eventId);
        edit_dayOfMonth_tooLow(accessTokenId, eventId);
        edit_dayOfMonth_tooHigh(accessTokenId, eventId);
    }

    private void edit_dayOfMonth_tooHigh(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .repetitionData(List.of(5, 32))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "repetitionData", "too high");
    }

    private void edit_dayOfMonth_tooLow(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .repetitionData(List.of(0, 5))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "repetitionData", "too low");
    }

    private void edit_dayOfMonth_repetitionDataContainsNull(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .repetitionData(CollectionUtils.toList(1, null))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "repetitionData", "must not contain null values");
    }

    private void edit_daysOfMonth_repetitionDataEmpty(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .repetitionData(List.of())
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "repetitionData", "must not be empty");
    }

    private void create_daysOfMonth_repetitionData(UUID accessTokenId) {
        create_nullRepetitionData(accessTokenId, RepetitionType.DAYS_OF_WEEK);
        create_invalidRepetitionData(accessTokenId, RepetitionType.DAYS_OF_WEEK);
        create_daysOfMonth_repetitionDataEmpty(accessTokenId);
        create_dayOfMonth_repetitionDataContainsNull(accessTokenId);
        create_dayOfMonth_tooLow(accessTokenId);
        create_dayOfMonth_tooHigh(accessTokenId);
    }

    private void create_dayOfMonth_tooHigh(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .repetitionData(List.of(5, 32))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "repetitionData", "too high");
    }

    private void create_dayOfMonth_tooLow(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .repetitionData(List.of(0, 5))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "repetitionData", "too low");
    }

    private void create_dayOfMonth_repetitionDataContainsNull(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .repetitionData(CollectionUtils.toList(1, null))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "repetitionData", "must not contain null values");
    }

    private void create_daysOfMonth_repetitionDataEmpty(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .repetitionData(List.of())
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "repetitionData", "must not be empty");
    }

    private void daysOfWeek(UUID accessTokenId) {
        create_generic(accessTokenId, RepetitionType.DAYS_OF_WEEK);
        create_endDate(accessTokenId, RepetitionType.DAYS_OF_WEEK);
        create_daysOfWeek_repetitionData(accessTokenId);
        UUID eventId = create(accessTokenId, RepetitionType.DAYS_OF_WEEK);
        edit_generic(accessTokenId, eventId, RepetitionType.DAYS_OF_WEEK);
        edit_endDate(accessTokenId, eventId, RepetitionType.DAYS_OF_WEEK);
        edit_daysOfWeek_repetitionData(accessTokenId, eventId);
        edit(accessTokenId, eventId, RepetitionType.DAYS_OF_WEEK);
    }

    private void edit_daysOfWeek_repetitionData(UUID accessTokenId, UUID eventId) {
        edit_nullRepetitionData(accessTokenId, eventId, RepetitionType.DAYS_OF_WEEK);
        edit_invalidRepetitionData(accessTokenId, eventId, RepetitionType.DAYS_OF_WEEK);
        edit_daysOfWeek_repetitionDataEmpty(accessTokenId, eventId);
        edit_daysOfWeek_repetitionDataContainsNull(accessTokenId, eventId);
    }

    private void edit_daysOfWeek_repetitionDataContainsNull(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_WEEK)
            .toBuilder()
            .repetitionData(CollectionUtils.toList(DayOfWeek.MONDAY, null))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "repetitionData", "must not contain null values");
    }

    private void edit_daysOfWeek_repetitionDataEmpty(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_WEEK)
            .toBuilder()
            .repetitionData(List.of())
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "repetitionData", "must not be empty");
    }

    private void create_daysOfWeek_repetitionData(UUID accessTokenId) {
        create_nullRepetitionData(accessTokenId, RepetitionType.DAYS_OF_WEEK);
        create_invalidRepetitionData(accessTokenId, RepetitionType.DAYS_OF_WEEK);
        create_daysOfWeek_repetitionDataEmpty(accessTokenId);
        create_daysOfWeek_repetitionDataContainsNull(accessTokenId);
    }

    private void create_daysOfWeek_repetitionDataContainsNull(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_WEEK)
            .toBuilder()
            .repetitionData(CollectionUtils.toList(DayOfWeek.MONDAY, null))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "repetitionData", "must not contain null values");
    }

    private void create_daysOfWeek_repetitionDataEmpty(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_WEEK)
            .toBuilder()
            .repetitionData(List.of())
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "repetitionData", "must not be empty");
    }

    private void everyXDays(UUID accessTokenId) {
        create_generic(accessTokenId, RepetitionType.EVERY_X_DAYS);
        create_endDate(accessTokenId, RepetitionType.EVERY_X_DAYS);
        create_everyXDays_repetitionData(accessTokenId);
        UUID eventId = create(accessTokenId, RepetitionType.EVERY_X_DAYS);
        edit_generic(accessTokenId, eventId, RepetitionType.EVERY_X_DAYS);
        edit_endDate(accessTokenId, eventId, RepetitionType.EVERY_X_DAYS);
        edit_everyXDays_repetitionData(accessTokenId, eventId);
        edit(accessTokenId, eventId, RepetitionType.EVERY_X_DAYS);
    }

    private void edit_everyXDays_repetitionData(UUID accessTokenId, UUID eventId) {
        edit_nullRepetitionData(accessTokenId, eventId, RepetitionType.EVERY_X_DAYS);
        edit_invalidRepetitionData(accessTokenId, eventId, RepetitionType.EVERY_X_DAYS);
        edit_repetitionDataTooLow(accessTokenId, eventId);
    }

    private void edit_repetitionDataTooLow(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.EVERY_X_DAYS)
            .toBuilder()
            .repetitionData(0)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "repetitionData", "too low");
    }

    private void edit_invalidRepetitionData(UUID accessTokenId, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .repetitionData("invalid")
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "repetitionData", "failed to parse");
    }

    private void edit_nullRepetitionData(UUID accessTokenId, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .repetitionData(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "repetitionData", "must not be null");
    }

    private void edit_endDate(UUID accessTokenId, UUID eventId, RepetitionType repetitionType) {
        edit_nullEndDate(accessTokenId, eventId, repetitionType);
        edit_endDateBeforeStartDate(accessTokenId, eventId, repetitionType);
        edit_eventDurationTooLong(accessTokenId, eventId, repetitionType);
    }

    private void edit_eventDurationTooLong(UUID accessTokenId, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .endDate(EventRequestFactory.DEFAULT_START_DATE.plusDays(EventRequestFactory.MAX_EVENT_DURATION_DAYS + 1))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "eventDuration", "too long");
    }

    private void edit_endDateBeforeStartDate(UUID accessTokenId, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().minusDays(1))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "startDate", "startDate cannot be after endDate");
    }

    private void edit_nullEndDate(UUID accessTokenId, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .endDate(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "endDate", "must not be null");
    }

    private void create_endDate(UUID accessTokenId, RepetitionType repetitionType) {
        create_nullEndDate(accessTokenId, repetitionType);
        create_endDateBeforeStartDate(accessTokenId, repetitionType);
        create_eventDurationTooLong(accessTokenId, repetitionType);
    }

    private void create_eventDurationTooLong(UUID accessTokenId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .endDate(EventRequestFactory.DEFAULT_START_DATE.plusDays(EventRequestFactory.MAX_EVENT_DURATION_DAYS + 1))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "eventDuration", "too long");
    }

    private void create_endDateBeforeStartDate(UUID accessTokenId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().minusDays(1))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "startDate", "startDate cannot be after endDate");
    }

    private void create_nullEndDate(UUID accessTokenId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .endDate(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "endDate", "must not be null");
    }

    private void create_everyXDays_repetitionData(UUID accessTokenId) {
        create_nullRepetitionData(accessTokenId, RepetitionType.EVERY_X_DAYS);
        create_invalidRepetitionData(accessTokenId, RepetitionType.EVERY_X_DAYS);
        create_repetitionDataTooLow(accessTokenId);
    }

    private void create_repetitionDataTooLow(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.EVERY_X_DAYS)
            .toBuilder()
            .repetitionData(0)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "repetitionData", "too low");
    }

    private void create_invalidRepetitionData(UUID accessTokenId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .repetitionData("invalid")
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "repetitionData", "failed to parse");
    }

    private void create_nullRepetitionData(UUID accessTokenId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .repetitionData(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "repetitionData", "must not be null");
    }

    private void oneTime(UUID accessTokenId) {
        create_generic(accessTokenId, RepetitionType.ONE_TIME);
        UUID eventId = create(accessTokenId, RepetitionType.ONE_TIME);
        edit_generic(accessTokenId, eventId, RepetitionType.ONE_TIME);
        edit(accessTokenId, eventId, RepetitionType.ONE_TIME);
    }

    private void edit(UUID accessTokenId, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.editRequest(repetitionType);

        CalendarEventActions.editEvent(getServerPort(), accessTokenId, eventId, request);
    }

    private void edit_generic(UUID accessTokenId, UUID eventId, RepetitionType repetitionType) {
        edit_nullRepetitionType(accessTokenId, eventId);
        edit_nullRepeatForDays(accessTokenId, eventId, repetitionType);
        edit_repeatForDaysTooLow(accessTokenId, eventId, repetitionType);
        edit_blankTitle(accessTokenId, eventId, repetitionType);
        edit_nullContent(accessTokenId, eventId, repetitionType);
        edit_nullRemindMeBeforeDays(accessTokenId, eventId, repetitionType);
        edit_remindMeBeforeDaysTooLow(accessTokenId, eventId, repetitionType);
        edit_nullLabels(accessTokenId, eventId, repetitionType);
        edit_labelsContainNull(accessTokenId, eventId, repetitionType);
        edit_labelDoesNotExist(accessTokenId, eventId, repetitionType);
    }

    private void edit_labelDoesNotExist(UUID accessTokenId, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .labels(List.of(UUID.randomUUID()))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "labelId", "does not exist");
    }

    private void edit_labelsContainNull(UUID accessTokenId, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .labels(CollectionUtils.toList(UUID.randomUUID(), null))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "labels", "must not contain null values");
    }

    private void edit_nullLabels(UUID accessTokenId, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .labels(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "labels", "must not be null");
    }

    private void edit_remindMeBeforeDaysTooLow(UUID accessTokenId, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .remindMeBeforeDays(-1)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "remindMeBeforeDays", "too low");
    }

    private void edit_nullRemindMeBeforeDays(UUID accessTokenId, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .remindMeBeforeDays(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "remindMeBeforeDays", "must not be null");
    }

    private void edit_nullContent(UUID accessTokenId, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .content(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "content", "must not be null");
    }

    private void edit_blankTitle(UUID accessTokenId, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .title(" ")
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "title", "must not be null or blank");
    }

    private void edit_repeatForDaysTooLow(UUID accessTokenId, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .repeatForDays(0)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "repeatForDays", "too low");
    }

    private void edit_nullRepeatForDays(UUID accessTokenId, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .repeatForDays(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "repeatForDays", "must not be null");
    }

    private void edit_nullRepetitionType(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .repetitionType(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "repetitionType", "must not be null");
    }

    private UUID create(UUID accessTokenId, RepetitionType repetitionType) {
        return CalendarEventActions.createEvent(getServerPort(), accessTokenId, EventRequestFactory.validRequest(repetitionType));
    }

    private void create_generic(UUID accessTokenId, RepetitionType repetitionType) {
        create_nullRepetitionType(accessTokenId);
        create_nullRepeatForDays(accessTokenId, repetitionType);
        create_repeatForDaysTooLow(accessTokenId, repetitionType);
        create_blankTitle(accessTokenId, repetitionType);
        create_nullContent(accessTokenId, repetitionType);
        create_nullRemindMeBeforeDays(accessTokenId, repetitionType);
        create_remindMeBeforeDaysTooLow(accessTokenId, repetitionType);
        create_nullLabels(accessTokenId, repetitionType);
        create_labelsContainNull(accessTokenId, repetitionType);
        create_labelDoesNotExist(accessTokenId, repetitionType);
    }

    private void create_labelDoesNotExist(UUID accessTokenId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .labels(List.of(UUID.randomUUID()))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "labelId", "does not exist");
    }

    private void create_labelsContainNull(UUID accessTokenId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .labels(CollectionUtils.toList(UUID.randomUUID(), null))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "labels", "must not contain null values");
    }

    private void create_nullLabels(UUID accessTokenId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .labels(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "labels", "must not be null");
    }

    private void create_remindMeBeforeDaysTooLow(UUID accessTokenId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .remindMeBeforeDays(-1)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "remindMeBeforeDays", "too low");
    }

    private void create_nullRemindMeBeforeDays(UUID accessTokenId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .remindMeBeforeDays(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "remindMeBeforeDays", "must not be null");
    }

    private void create_nullContent(UUID accessTokenId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .content(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "content", "must not be null");
    }

    private void create_blankTitle(UUID accessTokenId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .title(" ")
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "title", "must not be null or blank");
    }

    private void create_repeatForDaysTooLow(UUID accessTokenId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .repeatForDays(0)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "repeatForDays", "too low");
    }

    private void create_nullRepeatForDays(UUID accessTokenId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .repeatForDays(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "repeatForDays", "must not be null");
    }

    private void create_nullRepetitionType(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .repetitionType(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "repetitionType", "must not be null");
    }
}
