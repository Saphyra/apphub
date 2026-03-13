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
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        ResponseValidator.verifyInvalidParam(CalendarEventActions.getSearchResponse(getServerPort(), accessTokenId, "aa"), "searchText", "too short");

        UUID eventWithMatchingTitle = createEventWithMatchingTitle(accessTokenId);
        UUID eventWithMatchingContent = createEventWithMatchingContent(accessTokenId);
        UUID eventWithMatchingOccurrence = createEventWithMatchingOccurrence(accessTokenId);
        createUnmatchedEvent(accessTokenId);

        assertThat(CalendarEventActions.search(getServerPort(), accessTokenId, SEARCH_TEXT))
            .extracting(EventResponse::getEventId)
            .containsExactlyInAnyOrder(eventWithMatchingTitle, eventWithMatchingContent, eventWithMatchingOccurrence);
    }

    private void createUnmatchedEvent(UUID accessTokenId) {
        EventRequest eventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME);

        CalendarEventActions.createEvent(getServerPort(), accessTokenId, eventRequest);
    }

    private UUID createEventWithMatchingOccurrence(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .content(SEARCH_TEXT)
            .build();

        return CalendarEventActions.createEvent(getServerPort(), accessTokenId, request);
    }

    private UUID createEventWithMatchingContent(UUID accessTokenId) {
        EventRequest eventRequest = EventRequestFactory.validRequest(RepetitionType.ONE_TIME);

        UUID eventId = CalendarEventActions.createEvent(getServerPort(), accessTokenId, eventRequest);

        OccurrenceRequest occurrenceRequest = OccurrenceRequestFactory.editRequest()
            .toBuilder()
            .note(SEARCH_TEXT)
            .build();

        UUID occurrenceId = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessTokenId, eventId)
            .getFirst()
            .getOccurrenceId();

        CalendarOccurrenceActions.editOccurrence(getServerPort(), accessTokenId, occurrenceId, occurrenceRequest);

        return eventId;
    }

    private UUID createEventWithMatchingTitle(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .title(SEARCH_TEXT)
            .build();

        return CalendarEventActions.createEvent(getServerPort(), accessTokenId, request);
    }
}
