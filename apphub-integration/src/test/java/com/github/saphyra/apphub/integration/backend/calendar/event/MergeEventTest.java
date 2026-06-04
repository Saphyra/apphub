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
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID repeatingEventId = merge_notOneTimeEvent(accessToken);
        mergeEvents(accessToken, repeatingEventId);
    }

    private void mergeEvents(String accessToken, UUID repeatingEventId) {
        UUID parentId = createParent(accessToken);
        UUID originalOccurrenceId = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessToken, parentId)
            .getFirst()
            .getOccurrenceId();
        UUID childId = createChild(accessToken);
        UUID otherEventId = createOther(accessToken);

        CalendarEventActions.mergeEvents(getServerPort(), accessToken, parentId);

        assertThat(CalendarEventActions.getGetEventResponse(getServerPort(), accessToken, childId).statusCode()).isEqualTo(404);
        assertThat(CalendarEventActions.getGetEventResponse(getServerPort(), accessToken, otherEventId).statusCode()).isEqualTo(200);
        assertThat(CalendarEventActions.getGetEventResponse(getServerPort(), accessToken, repeatingEventId).statusCode()).isEqualTo(200);
        assertThat(CalendarEventActions.getGetEventResponse(getServerPort(), accessToken, parentId).statusCode()).isEqualTo(200);

        List<OccurrenceResponse> occurrences = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessToken, parentId);
        assertThat(occurrences).hasSize(2);
        CustomAssertions.singleListAssertThat(occurrences, occurrenceResponse -> !occurrenceResponse.getOccurrenceId().equals(originalOccurrenceId))
            .returns(OccurrenceRequestFactory.NEW_DATE, OccurrenceResponse::getDate)
            .returns(OccurrenceRequestFactory.NEW_TIME, OccurrenceResponse::getTime)
            .returns(OccurrenceRequestFactory.NEW_REMIND_ME_BEFORE_DAYS, OccurrenceResponse::getRemindMeBeforeDays)
            .returns(String.join("\n\n", EventRequestFactory.DEFAULT_CONTENT, OccurrenceRequestFactory.NEW_NOTE), OccurrenceResponse::getNote);
    }

    private UUID createOther(String accessToken) {
        EventRequest eventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .title("asd")
            .build();

        return CalendarEventActions.createEvent(getServerPort(), accessToken, eventRequest);
    }

    private UUID createChild(String accessToken) {
        EventRequest eventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .startDate(EventRequestFactory.NEW_START_DATE)
            .build();

        UUID eventId = CalendarEventActions.createEvent(getServerPort(), accessToken, eventRequest);

        UUID occurrenceId = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessToken, eventId)
            .getFirst()
            .getOccurrenceId();

        OccurrenceRequest occurrenceRequest = OccurrenceRequestFactory.editRequest();
        CalendarOccurrenceActions.editOccurrence(getServerPort(), accessToken, eventId, occurrenceId, occurrenceRequest);

        return eventId;
    }

    private UUID createParent(String accessToken) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .build();

        return CalendarEventActions.createEvent(getServerPort(), accessToken, request);
    }

    private UUID merge_notOneTimeEvent(String accessToken) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_WEEK);
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getMergeEventsResponse(getServerPort(), accessToken, eventId), "eventId", "invalid type");

        return eventId;
    }
}
