package com.github.saphyra.apphub.integration.backend.calendar;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarOccurrenceActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.action.backend.calendar.OccurrenceRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceStatus;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarOccurrenceTest extends BackEndTest {
    @Test(groups = {"be", "calendar"})
    public void occurrenceCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID eventId = CalendarEventActions.createEvent(getServerPort(), accessToken, EventRequestFactory.validRequest(RepetitionType.ONE_TIME));

        UUID occurrenceId = create(accessToken, eventId);
        edit(accessToken, eventId, occurrenceId);
        get(accessToken, eventId, occurrenceId);
        editStatus(accessToken, eventId, occurrenceId);
        setReminded(accessToken, eventId);
    }

    private void setReminded(String accessToken, UUID eventId) {
        OccurrenceRequest request = OccurrenceRequestFactory.validRequest()
            .toBuilder()
            .date(OccurrenceRequestFactory.DEFAULT_DATE.plusDays(1))
            .remindMeBeforeDays(2)
            .reminded(false)
            .build();

        UUID occurrenceId = CalendarOccurrenceActions.createOccurrence(getServerPort(), accessToken, eventId, request);

        OccurrenceResponse occurrenceResponse = CalendarOccurrenceActions.setReminded(getServerPort(), accessToken, eventId, occurrenceId);

        assertThat(occurrenceResponse.getReminded()).isTrue();
    }

    private void editStatus(String accessToken, UUID eventId, UUID occurrenceId) {
        editStatus_nullStatus(accessToken, eventId, occurrenceId);
        editStatus_valid(accessToken, eventId, occurrenceId);
    }

    private void editStatus_valid(String accessToken, UUID eventId, UUID occurrenceId) {
        assertThat(CalendarOccurrenceActions.editOccurrenceStatus(getServerPort(), accessToken, eventId, occurrenceId, OccurrenceStatus.SNOOZED))
            .returns(OccurrenceStatus.SNOOZED, OccurrenceResponse::getStatus);
    }

    private void editStatus_nullStatus(String accessToken, UUID eventId, UUID occurrenceId) {
        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getEditOccurrenceStatusResponse(getServerPort(), accessToken, eventId, occurrenceId, null), "status", "must not be null");
    }

    private void get(String accessToken, UUID eventId, UUID occurrenceId) {
        LocalDate startDate = OccurrenceRequestFactory.NEW_DATE.minusDays(1);
        LocalDate endDate = OccurrenceRequestFactory.NEW_DATE.plusDays(1);

        CustomAssertions.singleListAssertThat(CalendarOccurrenceActions.getOccurrences(getServerPort(), accessToken, startDate, endDate))
            .returns(occurrenceId, OccurrenceResponse::getOccurrenceId)
            .returns(eventId, OccurrenceResponse::getEventId);
    }

    private void edit(String accessToken, UUID eventId, UUID occurrenceId) {
        edit_nullDate(accessToken, eventId, occurrenceId);
        edit_nullStatus(accessToken, eventId, occurrenceId);
        edit_nullRemindMeBeforeDays(accessToken, eventId, occurrenceId);
        edit_remindMeBeforeDaysTooLow(accessToken, eventId, occurrenceId);
        edit_nullNote(accessToken, eventId, occurrenceId);
        edit_nullReminded(accessToken, eventId, occurrenceId);

        edit_valid(accessToken, eventId, occurrenceId);
    }

    private void edit_valid(String accessToken, UUID eventId, UUID occurrenceId) {
        OccurrenceRequest request = OccurrenceRequestFactory.editRequest();

        CalendarOccurrenceActions.editOccurrence(getServerPort(), accessToken, eventId, occurrenceId, request);

        CustomAssertions.singleListAssertThat(CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessToken, eventId), occurrenceResponse -> occurrenceResponse.getOccurrenceId().equals(occurrenceId))
            .returns(OccurrenceRequestFactory.NEW_DATE, OccurrenceResponse::getDate)
            .returns(OccurrenceRequestFactory.NEW_TIME, OccurrenceResponse::getTime)
            .returns(OccurrenceRequestFactory.NEW_STATUS, OccurrenceResponse::getStatus)
            .returns(OccurrenceRequestFactory.NEW_NOTE, OccurrenceResponse::getNote)
            .returns(OccurrenceRequestFactory.NEW_REMIND_ME_BEFORE_DAYS, OccurrenceResponse::getRemindMeBeforeDays)
            .returns(OccurrenceRequestFactory.NEW_REMINDED, OccurrenceResponse::getReminded);
    }

    private void edit_nullReminded(String accessToken, UUID eventId, UUID occurrenceId) {
        OccurrenceRequest request = OccurrenceRequestFactory.editRequest()
            .toBuilder()
            .reminded(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getEditOccurrenceResponse(getServerPort(), accessToken, eventId, occurrenceId, request), "reminded", "must not be null");
    }

    private void edit_nullNote(String accessToken, UUID eventId, UUID occurrenceId) {
        OccurrenceRequest request = OccurrenceRequestFactory.editRequest()
            .toBuilder()
            .note(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getEditOccurrenceResponse(getServerPort(), accessToken, eventId, occurrenceId, request), "note", "must not be null");
    }

    private void edit_remindMeBeforeDaysTooLow(String accessToken, UUID eventId, UUID occurrenceId) {
        OccurrenceRequest request = OccurrenceRequestFactory.editRequest()
            .toBuilder()
            .remindMeBeforeDays(-1)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getEditOccurrenceResponse(getServerPort(), accessToken, eventId, occurrenceId, request), "remindMeBeforeDays", "too low");
    }

    private void edit_nullRemindMeBeforeDays(String accessToken, UUID eventId, UUID occurrenceId) {
        OccurrenceRequest request = OccurrenceRequestFactory.editRequest()
            .toBuilder()
            .remindMeBeforeDays(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getEditOccurrenceResponse(getServerPort(), accessToken, eventId, occurrenceId, request), "remindMeBeforeDays", "must not be null");
    }

    private void edit_nullStatus(String accessToken, UUID eventId, UUID occurrenceId) {
        OccurrenceRequest request = OccurrenceRequestFactory.editRequest()
            .toBuilder()
            .status(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getEditOccurrenceResponse(getServerPort(), accessToken, eventId, occurrenceId, request), "status", "must not be null");
    }

    private void edit_nullDate(String accessToken, UUID eventId, UUID occurrenceId) {
        OccurrenceRequest request = OccurrenceRequestFactory.editRequest()
            .toBuilder()
            .date(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getEditOccurrenceResponse(getServerPort(), accessToken, eventId, occurrenceId, request), "date", "must not be null");
    }

    private UUID create(String accessToken, UUID eventId) {
        create_eventDoesNotExist(accessToken);
        create_nullDate(accessToken, eventId);
        create_nullStatus(accessToken, eventId);
        create_nullRemindMeBeforeDays(accessToken, eventId);
        create_remindMeBeforeDaysTooLow(accessToken, eventId);
        create_nullNote(accessToken, eventId);
        create_nullReminded(accessToken, eventId);

        return create_valid(accessToken, eventId);
    }

    private UUID create_valid(String accessToken, UUID eventId) {
        UUID occurrenceId = CalendarOccurrenceActions.createOccurrence(getServerPort(), accessToken, eventId, OccurrenceRequestFactory.validRequest());

        CustomAssertions.singleListAssertThat(CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessToken, eventId), occurrenceResponse -> occurrenceResponse.getOccurrenceId().equals(occurrenceId))
            .returns(OccurrenceRequestFactory.DEFAULT_DATE, OccurrenceResponse::getDate)
            .returns(OccurrenceRequestFactory.DEFAULT_TIME, OccurrenceResponse::getTime)
            .returns(OccurrenceRequestFactory.DEFAULT_STATUS, OccurrenceResponse::getStatus)
            .returns(OccurrenceRequestFactory.DEFAULT_NOTE, OccurrenceResponse::getNote)
            .returns(OccurrenceRequestFactory.DEFAULT_REMIND_ME_BEFORE_DAYS, OccurrenceResponse::getRemindMeBeforeDays)
            .returns(OccurrenceRequestFactory.DEFAULT_REMINDED, OccurrenceResponse::getReminded);

        return occurrenceId;
    }

    private void create_nullReminded(String accessToken, UUID eventId) {
        OccurrenceRequest request = OccurrenceRequestFactory.validRequest()
            .toBuilder()
            .reminded(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getCreateOccurrenceResponse(getServerPort(), accessToken, eventId, request), "reminded", "must not be null");
    }

    private void create_nullNote(String accessToken, UUID eventId) {
        OccurrenceRequest request = OccurrenceRequestFactory.validRequest()
            .toBuilder()
            .note(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getCreateOccurrenceResponse(getServerPort(), accessToken, eventId, request), "note", "must not be null");
    }

    private void create_remindMeBeforeDaysTooLow(String accessToken, UUID eventId) {
        OccurrenceRequest request = OccurrenceRequestFactory.validRequest()
            .toBuilder()
            .remindMeBeforeDays(-1)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getCreateOccurrenceResponse(getServerPort(), accessToken, eventId, request), "remindMeBeforeDays", "too low");
    }

    private void create_nullRemindMeBeforeDays(String accessToken, UUID eventId) {
        OccurrenceRequest request = OccurrenceRequestFactory.validRequest()
            .toBuilder()
            .remindMeBeforeDays(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getCreateOccurrenceResponse(getServerPort(), accessToken, eventId, request), "remindMeBeforeDays", "must not be null");
    }

    private void create_nullStatus(String accessToken, UUID eventId) {
        OccurrenceRequest request = OccurrenceRequestFactory.validRequest()
            .toBuilder()
            .status(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getCreateOccurrenceResponse(getServerPort(), accessToken, eventId, request), "status", "must not be null");
    }

    private void create_nullDate(String accessToken, UUID eventId) {
        OccurrenceRequest request = OccurrenceRequestFactory.validRequest()
            .toBuilder()
            .date(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getCreateOccurrenceResponse(getServerPort(), accessToken, eventId, request), "date", "must not be null");
    }

    private void create_eventDoesNotExist(String accessToken) {
        OccurrenceRequest request = OccurrenceRequestFactory.validRequest();

        ResponseValidator.verifyErrorResponse(CalendarOccurrenceActions.getCreateOccurrenceResponse(getServerPort(), accessToken, UUID.randomUUID(), request), 404, ErrorCode.DATA_NOT_FOUND);
    }
}
