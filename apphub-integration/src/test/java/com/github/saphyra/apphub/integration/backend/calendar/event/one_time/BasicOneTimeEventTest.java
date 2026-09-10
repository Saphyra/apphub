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
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID eventId = create(accessToken);
        edit(accessToken, eventId);
        delete(accessToken, eventId);
    }

    private void edit(String accessToken, UUID eventId) {
        EventRequest request = EventRequestFactory.editRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .title(NEW_TITLE)
            .content(NEW_CONTENT)
            .startDate(NEW_START_DATE)
            .time(NEW_TIME)
            .build();

        CalendarEventActions.editEvent(getServerPort(), accessToken, eventId, request);

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessToken, eventId))
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
            .returns(List.of(), EventResponse::getLabels)
            .returns(true,  EventResponse::getAutoDone);

        CustomAssertions.singleListAssertThat(CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessToken, eventId))
            .returns(eventId, OccurrenceResponse::getEventId)
            .returns(NEW_START_DATE, OccurrenceResponse::getDate)
            .returns(NEW_TIME, OccurrenceResponse::getTime)
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus)
            .returns(NEW_TITLE, OccurrenceResponse::getTitle)
            .returns(NEW_CONTENT, OccurrenceResponse::getContent)
            .returns("", OccurrenceResponse::getNote)
            .returns(0, OccurrenceResponse::getRemindMeBeforeDays)
            .returns(false, OccurrenceResponse::getReminded)
            .returns(true,  OccurrenceResponse::getAutoDone);
    }

    private void delete(String accessToken, UUID eventId) {
        CalendarEventActions.deleteEvent(getServerPort(), accessToken, eventId);

        assertThat(CalendarEventActions.getEvents(getServerPort(), accessToken)).isEmpty();
        assertThat(CalendarOccurrenceActions.getOccurrences(
            getServerPort(),
            accessToken,
            EventRequestFactory.DEFAULT_START_DATE.minusDays(10),
            EventRequestFactory.DEFAULT_START_DATE.plusDays(10)
        )).isEmpty();
    }

    private UUID create(String accessToken) {
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), accessToken, EventRequestFactory.validRequest(RepetitionType.ONE_TIME));

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessToken, eventId))
            .returns(eventId, EventResponse::getEventId)
            .returns(RepetitionType.ONE_TIME, EventResponse::getRepetitionType)
            .returns(null, EventResponse::getRepetitionData)
            .returns(1, EventResponse::getRepeatForDays)
            .returns(EventRequestFactory.DEFAULT_START_DATE, EventResponse::getStartDate)
            .returns(EventRequestFactory.DEFAULT_TIME, EventResponse::getTime)
            .returns(EventRequestFactory.DEFAULT_TITLE, EventResponse::getTitle)
            .returns(EventRequestFactory.DEFAULT_CONTENT, EventResponse::getContent)
            .returns(0, EventResponse::getRemindMeBeforeDays)
            .returns(List.of(), EventResponse::getLabels)
            .returns(false, EventResponse::getAutoDone);

        CustomAssertions.singleListAssertThat(CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessToken, eventId))
            .returns(eventId, OccurrenceResponse::getEventId)
            .returns(EventRequestFactory.DEFAULT_START_DATE, OccurrenceResponse::getDate)
            .returns(EventRequestFactory.DEFAULT_TIME, OccurrenceResponse::getTime)
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus)
            .returns(EventRequestFactory.DEFAULT_TITLE, OccurrenceResponse::getTitle)
            .returns(EventRequestFactory.DEFAULT_CONTENT, OccurrenceResponse::getContent)
            .returns("", OccurrenceResponse::getNote)
            .returns(0, OccurrenceResponse::getRemindMeBeforeDays)
            .returns(false, OccurrenceResponse::getReminded)
            .returns(false, OccurrenceResponse::getAutoDone);

        return eventId;
    }
}
