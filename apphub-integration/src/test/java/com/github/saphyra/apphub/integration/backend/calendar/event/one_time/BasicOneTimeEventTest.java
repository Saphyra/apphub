package com.github.saphyra.apphub.integration.backend.calendar.event.one_time;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarOccurrenceActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
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
    public void basicOneTimeEvent() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID eventId = create(accessTokenId);
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
}
