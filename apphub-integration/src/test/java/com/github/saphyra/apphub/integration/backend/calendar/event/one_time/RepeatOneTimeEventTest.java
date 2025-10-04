package com.github.saphyra.apphub.integration.backend.calendar.event.one_time;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarOccurrenceActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class RepeatOneTimeEventTest extends BackEndTest {
    private static final LocalDate NEW_START_DATE = EventRequestFactory.DEFAULT_START_DATE.plusDays(1);

    @Test(groups = {"be", "calendar"})
    public void repeatOneTimeEvent() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID eventId = create(accessTokenId);
        edit(accessTokenId, eventId);
    }

    private void edit(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .startDate(NEW_START_DATE)
            .repeatForDays(2)
            .build();

        CalendarEventActions.editEvent(getServerPort(), accessTokenId, eventId, request);

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId))
            .returns(NEW_START_DATE, EventResponse::getStartDate)
            .returns(2, EventResponse::getRepeatForDays);

        List<OccurrenceResponse> occurrences = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessTokenId, eventId);
        assertThat(occurrences)
            .extracting(OccurrenceResponse::getDate)
            .containsExactlyInAnyOrder(
                NEW_START_DATE,
                NEW_START_DATE.plusDays(1)
            );
    }

    private UUID create(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .repeatForDays(3)
            .build();

        UUID eventId = CalendarEventActions.createEvent(getServerPort(), accessTokenId, request);

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId).getRepeatForDays()).isEqualTo(3);

        List<OccurrenceResponse> occurrences = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessTokenId, eventId);
        assertThat(occurrences).hasSize(3)
            .extracting(OccurrenceResponse::getDate)
            .containsExactlyInAnyOrder(EventRequestFactory.DEFAULT_START_DATE, EventRequestFactory.DEFAULT_START_DATE.plusDays(1), EventRequestFactory.DEFAULT_START_DATE.plusDays(2));

        return eventId;
    }
}
