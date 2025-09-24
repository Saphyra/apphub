package com.github.saphyra.apphub.integration.backend.calendar.event.one_time;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarOccurrenceActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceStatus;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicOneTimeEventTest extends BackEndTest {
    private static final String NEW_TITLE = "new-title";
    private static final String NEW_CONTENT = "new-content";
    private static final LocalDate NEW_START_DATE = EventRequestFactory.DEFAULT_START_DATE.plusDays(1);
    private static final LocalTime NEW_TIME = EventRequestFactory.DEFAULT_TIME.plusHours(1);

    @Test(groups = {"be", "calendar"})
    public void oneTimeEvent() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_nullRepetitionType(accessTokenId);
        create_nullRepeatForDays(accessTokenId);
        create_repeatForDaysTooLow(accessTokenId);
        create_nullStartDate(accessTokenId);
        create_blankTitle(accessTokenId);
        create_nullContent(accessTokenId);
        create_nullRemindMeBeforeDays(accessTokenId);
        create_remindMeBeforeDaysTooLow(accessTokenId);
        create_nullLabels(accessTokenId);
        create_labelsContainNull(accessTokenId);
        create_labelDoesNotExist(accessTokenId);
        UUID eventId = create(accessTokenId);

        edit_nullRepetitionType(accessTokenId, eventId);
        edit_nullRepeatForDays(accessTokenId, eventId);
        edit_repeatForDaysTooLow(accessTokenId, eventId);
        edit_nullStartDate(accessTokenId, eventId);
        edit_blankTitle(accessTokenId, eventId);
        edit_nullContent(accessTokenId, eventId);
        edit_nullRemindMeBeforeDays(accessTokenId, eventId);
        edit_remindMeBeforeDaysTooLow(accessTokenId, eventId);
        edit_nullLabels(accessTokenId, eventId);
        edit_labelsContainNull(accessTokenId, eventId);
        edit_labelDoesNotExist(accessTokenId, eventId);
        edit(accessTokenId, eventId);

        delete(accessTokenId, eventId);
    }

    private void edit(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .title(NEW_TITLE)
            .content(NEW_CONTENT)
            .startDate(NEW_START_DATE)
            .time(NEW_TIME)
            .build();

        CalendarEventActions.editEvent(getServerPort(), accessTokenId, eventId, request);

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId))
            .returns(eventId, EventResponse::getEventId)
            .returns(RepetitionType.ONE_TIME, EventResponse::getRepetitionType)
            .returns(null, EventResponse::getRepetitionData)
            .returns(1, EventResponse::getRepeatForDays)
            .returns(NEW_START_DATE, EventResponse::getStartDate)
            .returns(null, EventResponse::getEndDate)
            .returns(NEW_TIME, EventResponse::getTime)
            .returns(NEW_TITLE, EventResponse::getTitle)
            .returns(NEW_CONTENT, EventResponse::getContent)
            .returns(0, EventResponse::getRemindMeBeforeDays)
            .returns(List.of(), EventResponse::getLabels);

        CustomAssertions.singleListAssertThat(CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessTokenId, eventId))
            .returns(eventId, OccurrenceResponse::getEventId)
            .returns(NEW_START_DATE, OccurrenceResponse::getDate)
            .returns(NEW_TIME, OccurrenceResponse::getTime)
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus)
            .returns(NEW_TITLE, OccurrenceResponse::getTitle)
            .returns(NEW_CONTENT, OccurrenceResponse::getContent)
            .returns("", OccurrenceResponse::getNote)
            .returns(0, OccurrenceResponse::getRemindMeBeforeDays)
            .returns(false, OccurrenceResponse::getReminded);
    }

    private void edit_labelDoesNotExist(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .labels(List.of(UUID.randomUUID()))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "labelId", "does not exist");
    }

    private void edit_labelsContainNull(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .labels(CollectionUtils.toList(null, UUID.randomUUID()))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "labels", "must not contain null values");
    }

    private void edit_nullLabels(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .labels(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "labels", "must not be null");
    }

    private void edit_remindMeBeforeDaysTooLow(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .remindMeBeforeDays(-1)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "remindMeBeforeDays", "too low");
    }

    private void edit_nullRemindMeBeforeDays(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .remindMeBeforeDays(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "remindMeBeforeDays", "must not be null");
    }

    private void edit_nullContent(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .content(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "content", "must not be null");
    }

    private void edit_blankTitle(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .title(" ")
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "title", "must not be null or blank");
    }

    private void edit_nullStartDate(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .startDate(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "startDate", "must not be null");
    }

    private void edit_repeatForDaysTooLow(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .repeatForDays(0)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, eventId, request), "repeatForDays", "too low");
    }

    private void edit_nullRepeatForDays(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
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

    private void delete(UUID accessTokenId, UUID eventId) {
        CalendarEventActions.deleteEvent(getServerPort(), accessTokenId, eventId);

        assertThat(CalendarEventActions.getEvents(getServerPort(), accessTokenId)).isEmpty();
        assertThat(CalendarOccurrenceActions.getOccurrences(
            getServerPort(),
            accessTokenId,
            EventRequestFactory.DEFAULT_START_DATE.minusDays(10),
            EventRequestFactory.DEFAULT_START_DATE.plusDays(10)
        )).isEmpty();
    }

    private UUID create(UUID accessTokenId) {
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), accessTokenId, EventRequestFactory.validRequest(RepetitionType.ONE_TIME));

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId))
            .returns(eventId, EventResponse::getEventId)
            .returns(RepetitionType.ONE_TIME, EventResponse::getRepetitionType)
            .returns(null, EventResponse::getRepetitionData)
            .returns(1, EventResponse::getRepeatForDays)
            .returns(EventRequestFactory.DEFAULT_START_DATE, EventResponse::getStartDate)
            .returns(null, EventResponse::getEndDate)
            .returns(EventRequestFactory.DEFAULT_TIME, EventResponse::getTime)
            .returns(EventRequestFactory.DEFAULT_TITLE, EventResponse::getTitle)
            .returns(EventRequestFactory.DEFAULT_CONTENT, EventResponse::getContent)
            .returns(0, EventResponse::getRemindMeBeforeDays)
            .returns(List.of(), EventResponse::getLabels);

        CustomAssertions.singleListAssertThat(CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessTokenId, eventId))
            .returns(eventId, OccurrenceResponse::getEventId)
            .returns(EventRequestFactory.DEFAULT_START_DATE, OccurrenceResponse::getDate)
            .returns(EventRequestFactory.DEFAULT_TIME, OccurrenceResponse::getTime)
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus)
            .returns(EventRequestFactory.DEFAULT_TITLE, OccurrenceResponse::getTitle)
            .returns(EventRequestFactory.DEFAULT_CONTENT, OccurrenceResponse::getContent)
            .returns("", OccurrenceResponse::getNote)
            .returns(0, OccurrenceResponse::getRemindMeBeforeDays)
            .returns(false, OccurrenceResponse::getReminded);

        return eventId;
    }

    private void create_labelDoesNotExist(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .labels(List.of(UUID.randomUUID()))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "labelId", "does not exist");
    }

    private void create_labelsContainNull(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .labels(CollectionUtils.toList(null, UUID.randomUUID()))
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "labels", "must not contain null values");
    }

    private void create_nullLabels(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .labels(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "labels", "must not be null");
    }

    private void create_remindMeBeforeDaysTooLow(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .remindMeBeforeDays(-1)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "remindMeBeforeDays", "too low");
    }

    private void create_nullRemindMeBeforeDays(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .remindMeBeforeDays(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "remindMeBeforeDays", "must not be null");
    }

    private void create_nullContent(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .content(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "content", "must not be null");
    }

    private void create_blankTitle(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .title(" ")
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "title", "must not be null or blank");
    }

    private void create_nullStartDate(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .startDate(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "startDate", "must not be null");
    }

    private void create_repeatForDaysTooLow(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .repeatForDays(0)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, request), "repeatForDays", "too low");
    }

    private void create_nullRepeatForDays(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
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
