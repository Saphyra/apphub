package com.github.saphyra.apphub.integration.backend.calendar.event;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarOccurrenceActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ArchiveEventTest extends BackEndTest {
    @Test(groups = {"be", "calendar"})
    public void archiveEvent() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        // Create a one-time event
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), accessTokenId, EventRequestFactory.validRequest(RepetitionType.ONE_TIME));

        // Archive the event
        CalendarEventActions.archiveEvent(getServerPort(), accessTokenId, eventId, true);

        // Verify event is archived
        EventResponse archivedEvent = CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId);
        assertThat(archivedEvent.getArchived()).isTrue();

        // Verify occurrences reflect archived flag
        List<OccurrenceResponse> occurrences = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessTokenId, eventId);
        CustomAssertions.singleListAssertThat(occurrences)
            .returns(true, OccurrenceResponse::getEventArchived);

        // Unarchive the event via editEvent
        CalendarEventActions.editEvent(getServerPort(), accessTokenId, eventId, EventRequestFactory.editRequest(RepetitionType.ONE_TIME));

        // Verify event is unarchived
        EventResponse unarchivedEvent = CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId);
        assertThat(unarchivedEvent.getArchived()).isFalse();

        // Verify occurrences reflect unarchived flag
        List<OccurrenceResponse> occurrencesAfterUnarchive = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessTokenId, eventId);
        CustomAssertions.singleListAssertThat(occurrencesAfterUnarchive)
            .returns(false, OccurrenceResponse::getEventArchived);
    }
}
