package com.github.saphyra.apphub.integration.backend.calendar.event;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.*;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class MergeEventTest extends BackEndTest {
    @Test(groups = {"be", "calendar"})
    public void mergeEventTest() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID repeatingEventId = merge_notOneTimeEvent(accessTokenId);
        mergeEvents(accessTokenId, repeatingEventId);
    }

    private void mergeEvents(UUID accessTokenId, UUID repeatingEventId) {
        UUID parentId = createParent(accessTokenId);
        UUID originalOccurrenceId = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessTokenId, parentId)
            .getFirst()
            .getOccurrenceId();
        UUID childId = createChild(accessTokenId);
        UUID otherEventId = createOther(accessTokenId);

        CalendarEventActions.mergeEvents(getServerPort(), accessTokenId, parentId);

        assertThat(CalendarEventActions.getGetEventResponse(getServerPort(), accessTokenId, childId).statusCode()).isEqualTo(404);
        assertThat(CalendarEventActions.getGetEventResponse(getServerPort(), accessTokenId, otherEventId).statusCode()).isEqualTo(200);
        assertThat(CalendarEventActions.getGetEventResponse(getServerPort(), accessTokenId, repeatingEventId).statusCode()).isEqualTo(200);
        assertThat(CalendarEventActions.getGetEventResponse(getServerPort(), accessTokenId, parentId).statusCode()).isEqualTo(200);

        List<OccurrenceResponse> occurrences = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessTokenId, parentId);
        assertThat(occurrences).hasSize(2);
        CustomAssertions.singleListAssertThat(occurrences, occurrenceResponse -> !occurrenceResponse.getOccurrenceId().equals(originalOccurrenceId))
            .returns(OccurrenceRequestFactory.NEW_DATE, OccurrenceResponse::getDate)
            .returns(OccurrenceRequestFactory.NEW_TIME, OccurrenceResponse::getTime)
            .returns(OccurrenceRequestFactory.NEW_REMIND_ME_BEFORE_DAYS, OccurrenceResponse::getRemindMeBeforeDays)
            .returns(String.join("\n\n", EventRequestFactory.DEFAULT_CONTENT, OccurrenceRequestFactory.NEW_NOTE), OccurrenceResponse::getNote);
    }

    private UUID createOther(UUID accessTokenId) {
        EventRequest eventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .title("asd")
            .build();

        return CalendarEventActions.createEvent(getServerPort(), accessTokenId, eventRequest);
    }

    private UUID createChild(UUID accessTokenId) {
        EventRequest eventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .startDate(EventRequestFactory.NEW_START_DATE)
            .build();

        UUID eventId = CalendarEventActions.createEvent(getServerPort(), accessTokenId, eventRequest);

        UUID occurrenceId = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessTokenId, eventId)
            .getFirst()
            .getOccurrenceId();

        OccurrenceRequest occurrenceRequest = OccurrenceRequestFactory.editRequest();
        CalendarOccurrenceActions.editOccurrence(getServerPort(), accessTokenId, occurrenceId, occurrenceRequest);

        return eventId;
    }

    private UUID createParent(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .build();

        return CalendarEventActions.createEvent(getServerPort(), accessTokenId, request);
    }

    private UUID merge_notOneTimeEvent(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_WEEK);
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), accessTokenId, request);

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getMergeEventsResponse(getServerPort(), accessTokenId, eventId), "eventId", "invalid type");

        return eventId;
    }
}
