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
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        oneTime(accessToken);
        everyXDays(accessToken);
        daysOfWeek(accessToken);
        daysOfMonth(accessToken);
    }

    private void daysOfMonth(String accessToken) {
        create_generic(accessToken, RepetitionType.DAYS_OF_MONTH);
        create_endDate(accessToken, RepetitionType.DAYS_OF_MONTH);
        create_daysOfMonth_repetitionData(accessToken);
        UUID eventId = create(accessToken, RepetitionType.DAYS_OF_MONTH);
        edit_generic(accessToken, eventId, RepetitionType.DAYS_OF_MONTH);
        edit_endDate(accessToken, eventId, RepetitionType.DAYS_OF_MONTH);
        edit_daysOfMonth_repetitionData(accessToken, eventId);
        edit(accessToken, eventId, RepetitionType.DAYS_OF_MONTH);
    }

    private void edit_daysOfMonth_repetitionData(String accessToken, UUID eventId) {
        edit_nullRepetitionData(accessToken, eventId, RepetitionType.DAYS_OF_MONTH);
        edit_invalidRepetitionData(accessToken, eventId, RepetitionType.DAYS_OF_MONTH);
        edit_daysOfMonth_repetitionDataEmpty(accessToken, eventId);
        edit_dayOfMonth_repetitionDataContainsNull(accessToken, eventId);
        edit_dayOfMonth_tooLow(accessToken, eventId);
        edit_dayOfMonth_tooHigh(accessToken, eventId);
    }

    private void edit_dayOfMonth_tooHigh(String accessToken, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .repetitionData(List.of(5, 32))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "repetitionData", "too high");
    }

    private void edit_dayOfMonth_tooLow(String accessToken, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .repetitionData(List.of(0, 5))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "repetitionData", "too low");
    }

    private void edit_dayOfMonth_repetitionDataContainsNull(String accessToken, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .repetitionData(CollectionUtils.toList(1, null))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "repetitionData", "must not contain null values");
    }

    private void edit_daysOfMonth_repetitionDataEmpty(String accessToken, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .repetitionData(List.of())
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "repetitionData", "must not be empty");
    }

    private void create_daysOfMonth_repetitionData(String accessToken) {
        create_nullRepetitionData(accessToken, RepetitionType.DAYS_OF_WEEK);
        create_invalidRepetitionData(accessToken, RepetitionType.DAYS_OF_WEEK);
        create_daysOfMonth_repetitionDataEmpty(accessToken);
        create_dayOfMonth_repetitionDataContainsNull(accessToken);
        create_dayOfMonth_tooLow(accessToken);
        create_dayOfMonth_tooHigh(accessToken);
    }

    private void create_dayOfMonth_tooHigh(String accessToken) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .repetitionData(List.of(5, 32))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "repetitionData", "too high");
    }

    private void create_dayOfMonth_tooLow(String accessToken) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .repetitionData(List.of(0, 5))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "repetitionData", "too low");
    }

    private void create_dayOfMonth_repetitionDataContainsNull(String accessToken) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .repetitionData(CollectionUtils.toList(1, null))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "repetitionData", "must not contain null values");
    }

    private void create_daysOfMonth_repetitionDataEmpty(String accessToken) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .repetitionData(List.of())
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "repetitionData", "must not be empty");
    }

    private void daysOfWeek(String accessToken) {
        create_generic(accessToken, RepetitionType.DAYS_OF_WEEK);
        create_endDate(accessToken, RepetitionType.DAYS_OF_WEEK);
        create_daysOfWeek_repetitionData(accessToken);
        UUID eventId = create(accessToken, RepetitionType.DAYS_OF_WEEK);
        edit_generic(accessToken, eventId, RepetitionType.DAYS_OF_WEEK);
        edit_endDate(accessToken, eventId, RepetitionType.DAYS_OF_WEEK);
        edit_daysOfWeek_repetitionData(accessToken, eventId);
        edit(accessToken, eventId, RepetitionType.DAYS_OF_WEEK);
    }

    private void edit_daysOfWeek_repetitionData(String accessToken, UUID eventId) {
        edit_nullRepetitionData(accessToken, eventId, RepetitionType.DAYS_OF_WEEK);
        edit_invalidRepetitionData(accessToken, eventId, RepetitionType.DAYS_OF_WEEK);
        edit_daysOfWeek_repetitionDataEmpty(accessToken, eventId);
        edit_daysOfWeek_repetitionDataContainsNull(accessToken, eventId);
    }

    private void edit_daysOfWeek_repetitionDataContainsNull(String accessToken, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_WEEK)
            .toBuilder()
            .repetitionData(CollectionUtils.toList(DayOfWeek.MONDAY, null))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "repetitionData", "must not contain null values");
    }

    private void edit_daysOfWeek_repetitionDataEmpty(String accessToken, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_WEEK)
            .toBuilder()
            .repetitionData(List.of())
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "repetitionData", "must not be empty");
    }

    private void create_daysOfWeek_repetitionData(String accessToken) {
        create_nullRepetitionData(accessToken, RepetitionType.DAYS_OF_WEEK);
        create_invalidRepetitionData(accessToken, RepetitionType.DAYS_OF_WEEK);
        create_daysOfWeek_repetitionDataEmpty(accessToken);
        create_daysOfWeek_repetitionDataContainsNull(accessToken);
    }

    private void create_daysOfWeek_repetitionDataContainsNull(String accessToken) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_WEEK)
            .toBuilder()
            .repetitionData(CollectionUtils.toList(DayOfWeek.MONDAY, null))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "repetitionData", "must not contain null values");
    }

    private void create_daysOfWeek_repetitionDataEmpty(String accessToken) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_WEEK)
            .toBuilder()
            .repetitionData(List.of())
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "repetitionData", "must not be empty");
    }

    private void everyXDays(String accessToken) {
        create_generic(accessToken, RepetitionType.EVERY_X_DAYS);
        create_endDate(accessToken, RepetitionType.EVERY_X_DAYS);
        create_everyXDays_repetitionData(accessToken);
        UUID eventId = create(accessToken, RepetitionType.EVERY_X_DAYS);
        edit_generic(accessToken, eventId, RepetitionType.EVERY_X_DAYS);
        edit_endDate(accessToken, eventId, RepetitionType.EVERY_X_DAYS);
        edit_everyXDays_repetitionData(accessToken, eventId);
        edit(accessToken, eventId, RepetitionType.EVERY_X_DAYS);
    }

    private void edit_everyXDays_repetitionData(String accessToken, UUID eventId) {
        edit_nullRepetitionData(accessToken, eventId, RepetitionType.EVERY_X_DAYS);
        edit_invalidRepetitionData(accessToken, eventId, RepetitionType.EVERY_X_DAYS);
        edit_repetitionDataTooLow(accessToken, eventId);
    }

    private void edit_repetitionDataTooLow(String accessToken, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.EVERY_X_DAYS)
            .toBuilder()
            .repetitionData(0)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "repetitionData", "too low");
    }

    private void edit_invalidRepetitionData(String accessToken, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .repetitionData("invalid")
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "repetitionData", "failed to parse");
    }

    private void edit_nullRepetitionData(String accessToken, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .repetitionData(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "repetitionData", "must not be null");
    }

    private void edit_endDate(String accessToken, UUID eventId, RepetitionType repetitionType) {
        edit_endDateBeforeStartDate(accessToken, eventId, repetitionType);
        edit_eventDurationTooLong(accessToken, eventId, repetitionType);
    }

    private void edit_eventDurationTooLong(String accessToken, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .endDate(EventRequestFactory.DEFAULT_START_DATE.plusDays(EventRequestFactory.MAX_EVENT_DURATION_DAYS + 1))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "eventDuration", "too long");
    }

    private void edit_endDateBeforeStartDate(String accessToken, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().minusDays(1))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "startDate", "startDate cannot be after endDate");
    }

    private void create_endDate(String accessToken, RepetitionType repetitionType) {
        create_endDateBeforeStartDate(accessToken, repetitionType);
        create_eventDurationTooLong(accessToken, repetitionType);
    }

    private void create_eventDurationTooLong(String accessToken, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .endDate(EventRequestFactory.DEFAULT_START_DATE.plusDays(EventRequestFactory.MAX_EVENT_DURATION_DAYS + 1))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "eventDuration", "too long");
    }

    private void create_endDateBeforeStartDate(String accessToken, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().minusDays(1))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "startDate", "startDate cannot be after endDate");
    }

    private void create_everyXDays_repetitionData(String accessToken) {
        create_nullRepetitionData(accessToken, RepetitionType.EVERY_X_DAYS);
        create_invalidRepetitionData(accessToken, RepetitionType.EVERY_X_DAYS);
        create_repetitionDataTooLow(accessToken);
    }

    private void create_repetitionDataTooLow(String accessToken) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.EVERY_X_DAYS)
            .toBuilder()
            .repetitionData(0)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "repetitionData", "too low");
    }

    private void create_invalidRepetitionData(String accessToken, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .repetitionData("invalid")
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "repetitionData", "failed to parse");
    }

    private void create_nullRepetitionData(String accessToken, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .repetitionData(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "repetitionData", "must not be null");
    }

    private void oneTime(String accessToken) {
        create_generic(accessToken, RepetitionType.ONE_TIME);
        UUID eventId = create(accessToken, RepetitionType.ONE_TIME);
        edit_generic(accessToken, eventId, RepetitionType.ONE_TIME);
        edit(accessToken, eventId, RepetitionType.ONE_TIME);
    }

    private void edit(String accessToken, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.editRequest(repetitionType);

        CalendarEventActions.editEvent(getServerPort(), accessToken, eventId, request);
    }

    private void edit_generic(String accessToken, UUID eventId, RepetitionType repetitionType) {
        edit_nullRepetitionType(accessToken, eventId);
        edit_nullRepeatForDays(accessToken, eventId, repetitionType);
        edit_repeatForDaysTooLow(accessToken, eventId, repetitionType);
        edit_blankTitle(accessToken, eventId, repetitionType);
        edit_nullContent(accessToken, eventId, repetitionType);
        edit_nullRemindMeBeforeDays(accessToken, eventId, repetitionType);
        edit_remindMeBeforeDaysTooLow(accessToken, eventId, repetitionType);
        edit_nullLabels(accessToken, eventId, repetitionType);
        edit_labelsContainNull(accessToken, eventId, repetitionType);
        edit_labelDoesNotExist(accessToken, eventId, repetitionType);
        edit_nullArchived(accessToken, eventId, repetitionType);
    }

    private void edit_labelDoesNotExist(String accessToken, UUID eventId, RepetitionType repetitionType) {
        UUID labelId = UUID.randomUUID();
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .labels(List.of(labelId))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "labels", "Unsupported values: " + List.of(labelId));
    }

    private void edit_labelsContainNull(String accessToken, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .labels(CollectionUtils.toList(UUID.randomUUID(), null))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "labels", "must not contain null values");
    }

    private void edit_nullLabels(String accessToken, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .labels(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "labels", "must not be null");
    }

    private void edit_remindMeBeforeDaysTooLow(String accessToken, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .remindMeBeforeDays(-1)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "remindMeBeforeDays", "too low");
    }

    private void edit_nullRemindMeBeforeDays(String accessToken, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .remindMeBeforeDays(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "remindMeBeforeDays", "must not be null");
    }

    private void edit_nullContent(String accessToken, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .content(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "content", "must not be null");
    }

    private void edit_blankTitle(String accessToken, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .title(" ")
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "title", "must not be null or blank");
    }

    private void edit_repeatForDaysTooLow(String accessToken, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .repeatForDays(0)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "repeatForDays", "too low");
    }

    private void edit_nullRepeatForDays(String accessToken, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .repeatForDays(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "repeatForDays", "must not be null");
    }

    private void edit_nullRepetitionType(String accessToken, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .repetitionType(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "repetitionType", "must not be null");
    }

    private UUID create(String accessToken, RepetitionType repetitionType) {
        return CalendarEventActions.createEvent(getServerPort(), accessToken, EventRequestFactory.validRequest(repetitionType));
    }

    private void create_generic(String accessToken, RepetitionType repetitionType) {
        create_nullRepetitionType(accessToken);
        create_nullRepeatForDays(accessToken, repetitionType);
        create_repeatForDaysTooLow(accessToken, repetitionType);
        create_blankTitle(accessToken, repetitionType);
        create_nullContent(accessToken, repetitionType);
        create_nullRemindMeBeforeDays(accessToken, repetitionType);
        create_remindMeBeforeDaysTooLow(accessToken, repetitionType);
        create_nullLabels(accessToken, repetitionType);
        create_labelsContainNull(accessToken, repetitionType);
        create_labelDoesNotExist(accessToken, repetitionType);
    }

    private void create_labelDoesNotExist(String accessToken, RepetitionType repetitionType) {
        UUID labelId = UUID.randomUUID();
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .labels(List.of(labelId))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "labels", "Unsupported values: " + List.of(labelId));
    }

    private void create_labelsContainNull(String accessToken, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .labels(CollectionUtils.toList(UUID.randomUUID(), null))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "labels", "must not contain null values");
    }

    private void create_nullLabels(String accessToken, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .labels(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "labels", "must not be null");
    }

    private void create_remindMeBeforeDaysTooLow(String accessToken, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .remindMeBeforeDays(-1)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "remindMeBeforeDays", "too low");
    }

    private void create_nullRemindMeBeforeDays(String accessToken, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .remindMeBeforeDays(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "remindMeBeforeDays", "must not be null");
    }

    private void create_nullContent(String accessToken, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .content(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "content", "must not be null");
    }

    private void create_blankTitle(String accessToken, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .title(" ")
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "title", "must not be null or blank");
    }

    private void create_repeatForDaysTooLow(String accessToken, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .repeatForDays(0)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "repeatForDays", "too low");
    }

    private void create_nullRepeatForDays(String accessToken, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .repeatForDays(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "repeatForDays", "must not be null");
    }

    private void create_nullRepetitionType(String accessToken) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .repetitionType(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, request), "repetitionType", "must not be null");
    }

    private void edit_nullArchived(String accessToken, UUID eventId, RepetitionType repetitionType) {
        EventRequest request = EventRequestFactory.validRequest(repetitionType)
            .toBuilder()
            .archived(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, eventId, request), "archived", "must not be null");
    }
}
