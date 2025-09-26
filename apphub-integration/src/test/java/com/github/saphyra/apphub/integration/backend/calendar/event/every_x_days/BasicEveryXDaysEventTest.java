package com.github.saphyra.apphub.integration.backend.calendar.event.every_x_days;

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
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicEveryXDaysEventTest extends BackEndTest {
    private static final String NEW_TITLE = "new-title";
    private static final String NEW_CONTENT = "new-content";
    private static final LocalDate NEW_START_DATE = EventRequestFactory.DEFAULT_START_DATE.plusDays(1);
    private static final LocalTime NEW_TIME = EventRequestFactory.DEFAULT_TIME.plusHours(1);
    private static final LocalDate NEW_END_DATE = NEW_START_DATE.plusDays(EventRequestFactory.DEFAULT_EVENT_DURATION);
    private static final Integer NEW_REPETITION_DATA = 3;

    @Test(groups = {"be", "calendar"})
    public void basicEveryXDaysEvent() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID eventId = create(accessTokenId);
        eventId = edit(accessTokenId, eventId);
        delete(accessTokenId, eventId);
    }

    private void delete(UUID accessTokenId, UUID eventId) {
        CalendarEventActions.deleteEvent(getServerPort(), accessTokenId, eventId);

        assertThat(CalendarEventActions.getEvents(getServerPort(), accessTokenId)).isEmpty();
        assertThat(CalendarOccurrenceActions.getOccurrences(
            getServerPort(),
            accessTokenId,
            EventRequestFactory.DEFAULT_START_DATE.minusDays(10),
            EventRequestFactory.DEFAULT_END_DATE.plusDays(10)
        )).isEmpty();
    }

    private UUID edit(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.EVERY_X_DAYS)
            .toBuilder()
            .title(NEW_TITLE)
            .content(NEW_CONTENT)
            .startDate(NEW_START_DATE)
            .endDate(NEW_END_DATE)
            .time(NEW_TIME)
            .repetitionData(NEW_REPETITION_DATA)
            .build();

        CalendarEventActions.editEvent(getServerPort(), accessTokenId, eventId, request);

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId))
            .returns(eventId, EventResponse::getEventId)
            .returns(RepetitionType.EVERY_X_DAYS, EventResponse::getRepetitionType)
            .returns(NEW_REPETITION_DATA, EventResponse::getRepetitionData)
            .returns(1, EventResponse::getRepeatForDays)
            .returns(NEW_START_DATE, EventResponse::getStartDate)
            .returns(NEW_END_DATE, EventResponse::getEndDate)
            .returns(NEW_TIME, EventResponse::getTime)
            .returns(NEW_TITLE, EventResponse::getTitle)
            .returns(NEW_CONTENT, EventResponse::getContent)
            .returns(0, EventResponse::getRemindMeBeforeDays)
            .returns(List.of(), EventResponse::getLabels);

        List<LocalDate> occurrences = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessTokenId, eventId)
            .stream()
            .map(OccurrenceResponse::getDate)
            .toList();

        assertThat(occurrences).hasSize(7);

        for (int i = 0; NEW_START_DATE.plusDays(i).isBefore(NEW_END_DATE); i++) {
            LocalDate date = NEW_START_DATE.plusDays(i);
            boolean shouldHaveOccurrence = i % NEW_REPETITION_DATA == 0;
            if (shouldHaveOccurrence) {
                assertThat(occurrences).contains(date);
            } else {
                assertThat(occurrences).doesNotContain(date);
            }
        }

        return eventId;
    }

    private UUID create(UUID accessTokenId) {
        UUID eventId = CalendarEventActions.createEvent(getServerPort(), accessTokenId, EventRequestFactory.validRequest(RepetitionType.EVERY_X_DAYS));

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId))
            .returns(eventId, EventResponse::getEventId)
            .returns(RepetitionType.EVERY_X_DAYS, EventResponse::getRepetitionType)
            .returns(EventRequestFactory.DEFAULT_REPETITION_DATA_EVERY_X_DAYS, EventResponse::getRepetitionData)
            .returns(1, EventResponse::getRepeatForDays)
            .returns(EventRequestFactory.DEFAULT_START_DATE, EventResponse::getStartDate)
            .returns(EventRequestFactory.DEFAULT_END_DATE, EventResponse::getEndDate)
            .returns(EventRequestFactory.DEFAULT_TIME, EventResponse::getTime)
            .returns(EventRequestFactory.DEFAULT_TITLE, EventResponse::getTitle)
            .returns(EventRequestFactory.DEFAULT_CONTENT, EventResponse::getContent)
            .returns(0, EventResponse::getRemindMeBeforeDays)
            .returns(List.of(), EventResponse::getLabels);

        List<LocalDate> occurrences = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessTokenId, eventId)
            .stream()
            .map(OccurrenceResponse::getDate)
            .toList();

        assertThat(occurrences).hasSize(6);

        for (int i = 0; EventRequestFactory.DEFAULT_START_DATE.plusDays(i).isBefore(EventRequestFactory.DEFAULT_END_DATE); i++) {
            LocalDate date = EventRequestFactory.DEFAULT_START_DATE.plusDays(i);
            boolean shouldHaveOccurrence = i % EventRequestFactory.DEFAULT_REPETITION_DATA_EVERY_X_DAYS == 0;
            if (shouldHaveOccurrence) {
                assertThat(occurrences).contains(date);
            } else {
                assertThat(occurrences).doesNotContain(date);
            }
        }

        return eventId;
    }
}
