package com.github.saphyra.apphub.integration.backend.calendar.event;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarOccurrenceActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.action.backend.calendar.OccurrenceRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarSearchEventsTest extends BackEndTest {
    private static final String SEARCH_TEXT = "search-text";

    @Test(groups = {"be", "calendar"})
    public void searchEventsTest() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getSearchResponse(getServerPort(), accessToken, "aa"), "searchText", "too short");

        UUID eventWithMatchingTitle = createEventWithMatchingTitle(accessToken);
        UUID eventWithMatchingContent = createEventWithMatchingContent(accessToken);
        UUID eventWithMatchingOccurrence = createEventWithMatchingOccurrence(accessToken);
        createUnmatchedEvent(accessToken);

        assertThat(CalendarEventActions.search(getServerPort(), accessToken, SEARCH_TEXT))
            .extracting(EventResponse::getEventId)
            .containsExactlyInAnyOrder(eventWithMatchingTitle, eventWithMatchingContent, eventWithMatchingOccurrence);
    }

    private void createUnmatchedEvent(String accessToken) {
        EventRequest eventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME);

        CalendarEventActions.createEvent(getServerPort(), accessToken, eventRequest);
    }

    private UUID createEventWithMatchingOccurrence(String accessToken) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .content(SEARCH_TEXT)
            .build();

        return CalendarEventActions.createEvent(getServerPort(), accessToken, request);
    }

    private UUID createEventWithMatchingContent(String accessToken) {
        EventRequest eventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME);

        UUID eventId = CalendarEventActions.createEvent(getServerPort(), accessToken, eventRequest);

        OccurrenceRequest occurrenceRequest = OccurrenceRequestFactory.editRequest()
            .toBuilder()
            .note(SEARCH_TEXT)
            .build();

        UUID occurrenceId = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessToken, eventId)
            .getFirst()
            .getOccurrenceId();

        CalendarOccurrenceActions.editOccurrence(getServerPort(), accessToken, occurrenceId, occurrenceRequest);

        return eventId;
    }

    private UUID createEventWithMatchingTitle(String accessToken) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .title(SEARCH_TEXT)
            .build();

        return CalendarEventActions.createEvent(getServerPort(), accessToken, request);
    }
}
