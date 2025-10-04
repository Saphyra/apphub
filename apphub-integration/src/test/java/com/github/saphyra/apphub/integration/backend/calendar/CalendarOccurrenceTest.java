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
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID eventId = CalendarEventActions.createEvent(getServerPort(), accessTokenId, EventRequestFactory.validRequest(RepetitionType.ONE_TIME));

        UUID occurrenceId = create(accessTokenId, eventId);
        edit(accessTokenId, eventId, occurrenceId);
        get(accessTokenId, eventId, occurrenceId);
        editStatus(accessTokenId, occurrenceId);
        setReminded(accessTokenId, eventId);
    }

    private void setReminded(UUID accessTokenId, UUID eventId) {
        OccurrenceRequest request = OccurrenceRequestFactory.validRequest()
            .toBuilder()
            .date(OccurrenceRequestFactory.DEFAULT_DATE.plusDays(1))
            .remindMeBeforeDays(2)
            .reminded(false)
            .build();

        UUID occurrenceId = CalendarOccurrenceActions.createOccurrence(getServerPort(), accessTokenId, eventId, request);

        OccurrenceResponse occurrenceResponse = CalendarOccurrenceActions.setReminded(getServerPort(), accessTokenId, occurrenceId);

        assertThat(occurrenceResponse.getReminded()).isTrue();
    }

    private void editStatus(UUID accessTokenId, UUID occurrenceId) {
        editStatus_nullStatus(accessTokenId, occurrenceId);
        editStatus_valid(accessTokenId, occurrenceId);
    }

    private void editStatus_valid(UUID accessTokenId, UUID occurrenceId) {
        assertThat(CalendarOccurrenceActions.editOccurrenceStatus(getServerPort(), accessTokenId, occurrenceId, OccurrenceStatus.SNOOZED))
            .returns(OccurrenceStatus.SNOOZED, OccurrenceResponse::getStatus);
    }

    private void editStatus_nullStatus(UUID accessTokenId, UUID occurrenceId) {
        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getEditOccurrenceStatusResponse(getServerPort(), accessTokenId, occurrenceId, null), "status", "must not be null");
    }

    private void get(UUID accessTokenId, UUID eventId, UUID occurrenceId) {
        LocalDate startDate = OccurrenceRequestFactory.NEW_DATE.minusDays(1);
        LocalDate endDate = OccurrenceRequestFactory.NEW_DATE.plusDays(1);

        CustomAssertions.singleListAssertThat(CalendarOccurrenceActions.getOccurrences(getServerPort(), accessTokenId, startDate, endDate))
            .returns(occurrenceId, OccurrenceResponse::getOccurrenceId)
            .returns(eventId, OccurrenceResponse::getEventId);
    }

    private void edit(UUID accessTokenId, UUID eventId, UUID occurrenceId) {
        edit_nullDate(accessTokenId, occurrenceId);
        edit_nullStatus(accessTokenId, occurrenceId);
        edit_nullRemindMeBeforeDays(accessTokenId, occurrenceId);
        edit_remindMeBeforeDaysTooLow(accessTokenId, occurrenceId);
        edit_nullNote(accessTokenId, occurrenceId);
        edit_nullReminded(accessTokenId, occurrenceId);

        edit_valid(accessTokenId, eventId, occurrenceId);
    }

    private void edit_valid(UUID accessTokenId, UUID eventId, UUID occurrenceId) {
        OccurrenceRequest request = OccurrenceRequestFactory.editRequest();

        CalendarOccurrenceActions.editOccurrence(getServerPort(), accessTokenId, occurrenceId, request);

        CustomAssertions.singleListAssertThat(CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessTokenId, eventId), occurrenceResponse -> occurrenceResponse.getOccurrenceId().equals(occurrenceId))
            .returns(OccurrenceRequestFactory.NEW_DATE, OccurrenceResponse::getDate)
            .returns(OccurrenceRequestFactory.NEW_TIME, OccurrenceResponse::getTime)
            .returns(OccurrenceRequestFactory.NEW_STATUS, OccurrenceResponse::getStatus)
            .returns(OccurrenceRequestFactory.NEW_NOTE, OccurrenceResponse::getNote)
            .returns(OccurrenceRequestFactory.NEW_REMIND_ME_BEFORE_DAYS, OccurrenceResponse::getRemindMeBeforeDays)
            .returns(OccurrenceRequestFactory.NEW_REMINDED, OccurrenceResponse::getReminded);
    }

    private void edit_nullReminded(UUID accessTokenId, UUID occurrenceId) {
        OccurrenceRequest request = OccurrenceRequestFactory.editRequest()
            .toBuilder()
            .reminded(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getEditOccurrenceResponse(getServerPort(), accessTokenId, occurrenceId, request), "reminded", "must not be null");
    }

    private void edit_nullNote(UUID accessTokenId, UUID occurrenceId) {
        OccurrenceRequest request = OccurrenceRequestFactory.editRequest()
            .toBuilder()
            .note(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getEditOccurrenceResponse(getServerPort(), accessTokenId, occurrenceId, request), "note", "must not be null");
    }

    private void edit_remindMeBeforeDaysTooLow(UUID accessTokenId, UUID occurrenceId) {
        OccurrenceRequest request = OccurrenceRequestFactory.editRequest()
            .toBuilder()
            .remindMeBeforeDays(-1)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getEditOccurrenceResponse(getServerPort(), accessTokenId, occurrenceId, request), "remindMeBeforeDays", "too low");
    }

    private void edit_nullRemindMeBeforeDays(UUID accessTokenId, UUID occurrenceId) {
        OccurrenceRequest request = OccurrenceRequestFactory.editRequest()
            .toBuilder()
            .remindMeBeforeDays(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getEditOccurrenceResponse(getServerPort(), accessTokenId, occurrenceId, request), "remindMeBeforeDays", "must not be null");
    }

    private void edit_nullStatus(UUID accessTokenId, UUID occurrenceId) {
        OccurrenceRequest request = OccurrenceRequestFactory.editRequest()
            .toBuilder()
            .status(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getEditOccurrenceResponse(getServerPort(), accessTokenId, occurrenceId, request), "status", "must not be null");
    }

    private void edit_nullDate(UUID accessTokenId, UUID occurrenceId) {
        OccurrenceRequest request = OccurrenceRequestFactory.editRequest()
            .toBuilder()
            .date(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getEditOccurrenceResponse(getServerPort(), accessTokenId, occurrenceId, request), "date", "must not be null");
    }

    private UUID create(UUID accessTokenId, UUID eventId) {
        create_eventDoesNotExist(accessTokenId);
        create_nullDate(accessTokenId, eventId);
        create_nullStatus(accessTokenId, eventId);
        create_nullRemindMeBeforeDays(accessTokenId, eventId);
        create_remindMeBeforeDaysTooLow(accessTokenId, eventId);
        create_nullNote(accessTokenId, eventId);
        create_nullReminded(accessTokenId, eventId);

        return create_valid(accessTokenId, eventId);
    }

    private UUID create_valid(UUID accessTokenId, UUID eventId) {
        UUID occurrenceId = CalendarOccurrenceActions.createOccurrence(getServerPort(), accessTokenId, eventId, OccurrenceRequestFactory.validRequest());

        CustomAssertions.singleListAssertThat(CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessTokenId, eventId), occurrenceResponse -> occurrenceResponse.getOccurrenceId().equals(occurrenceId))
            .returns(OccurrenceRequestFactory.DEFAULT_DATE, OccurrenceResponse::getDate)
            .returns(OccurrenceRequestFactory.DEFAULT_TIME, OccurrenceResponse::getTime)
            .returns(OccurrenceRequestFactory.DEFAULT_STATUS, OccurrenceResponse::getStatus)
            .returns(OccurrenceRequestFactory.DEFAULT_NOTE, OccurrenceResponse::getNote)
            .returns(OccurrenceRequestFactory.DEFAULT_REMIND_ME_BEFORE_DAYS, OccurrenceResponse::getRemindMeBeforeDays)
            .returns(OccurrenceRequestFactory.DEFAULT_REMINDED, OccurrenceResponse::getReminded);

        return occurrenceId;
    }

    private void create_nullReminded(UUID accessTokenId, UUID eventId) {
        OccurrenceRequest request = OccurrenceRequestFactory.validRequest()
            .toBuilder()
            .reminded(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getCreateOccurrenceResponse(getServerPort(), accessTokenId, eventId, request), "reminded", "must not be null");
    }

    private void create_nullNote(UUID accessTokenId, UUID eventId) {
        OccurrenceRequest request = OccurrenceRequestFactory.validRequest()
            .toBuilder()
            .note(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getCreateOccurrenceResponse(getServerPort(), accessTokenId, eventId, request), "note", "must not be null");
    }

    private void create_remindMeBeforeDaysTooLow(UUID accessTokenId, UUID eventId) {
        OccurrenceRequest request = OccurrenceRequestFactory.validRequest()
            .toBuilder()
            .remindMeBeforeDays(-1)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getCreateOccurrenceResponse(getServerPort(), accessTokenId, eventId, request), "remindMeBeforeDays", "too low");
    }

    private void create_nullRemindMeBeforeDays(UUID accessTokenId, UUID eventId) {
        OccurrenceRequest request = OccurrenceRequestFactory.validRequest()
            .toBuilder()
            .remindMeBeforeDays(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getCreateOccurrenceResponse(getServerPort(), accessTokenId, eventId, request), "remindMeBeforeDays", "must not be null");
    }

    private void create_nullStatus(UUID accessTokenId, UUID eventId) {
        OccurrenceRequest request = OccurrenceRequestFactory.validRequest()
            .toBuilder()
            .status(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getCreateOccurrenceResponse(getServerPort(), accessTokenId, eventId, request), "status", "must not be null");
    }

    private void create_nullDate(UUID accessTokenId, UUID eventId) {
        OccurrenceRequest request = OccurrenceRequestFactory.validRequest()
            .toBuilder()
            .date(null)
            .build();

        ResponseValidator.verifyInvalidParam(CalendarOccurrenceActions.getCreateOccurrenceResponse(getServerPort(), accessTokenId, eventId, request), "date", "must not be null");
    }

    private void create_eventDoesNotExist(UUID accessTokenId) {
        OccurrenceRequest request = OccurrenceRequestFactory.validRequest();

        ResponseValidator.verifyErrorResponse(CalendarOccurrenceActions.getCreateOccurrenceResponse(getServerPort(), accessTokenId, UUID.randomUUID(), request), 404, ErrorCode.DATA_NOT_FOUND);
    }
}
