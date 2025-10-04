package com.github.saphyra.apphub.integration.backend.calendar.event.days_of_week;

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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicDaysOfWeekEventTest extends BackEndTest {
    private static final LocalDate START_DATE = LocalDate.now()
        .plusWeeks(1)
        .with(DayOfWeek.MONDAY);
    private static final LocalDate END_DATE = START_DATE.plusWeeks(1)
        .with(DayOfWeek.SUNDAY);
    private static final LocalDate NEW_START_DATE = START_DATE.plusWeeks(1);
    private static final LocalDate NEW_END_DATE = NEW_START_DATE.plusWeeks(1)
        .with(DayOfWeek.SUNDAY);

    @Test(groups = {"be", "calendar"})
    public void basicDaysOfWeekEvent() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID eventId = create(accessTokenId);
        edit(accessTokenId, eventId);
        delete(accessTokenId, eventId);
    }

    private void delete(UUID accessTokenId, UUID eventId) {
        CalendarEventActions.deleteEvent(getServerPort(), accessTokenId, eventId);

        assertThat(CalendarEventActions.getEvents(getServerPort(), accessTokenId)).isEmpty();
        assertThat(CalendarOccurrenceActions.getOccurrences(
            getServerPort(),
            accessTokenId,
            START_DATE.minusDays(10),
            NEW_END_DATE.plusDays(10)
        )).isEmpty();
    }

    private void edit(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.editRequest(RepetitionType.DAYS_OF_WEEK)
            .toBuilder()
            .startDate(NEW_START_DATE)
            .endDate(NEW_END_DATE)
            .build();

        CalendarEventActions.editEvent(getServerPort(), accessTokenId, eventId, request);

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId))
            .returns(eventId, EventResponse::getEventId)
            .returns(RepetitionType.DAYS_OF_WEEK, EventResponse::getRepetitionType)
            .returns(EventRequestFactory.NEW_REPETITION_DATA_DAYS_OF_WEEK, EventResponse::getRepetitionData)
            .returns(NEW_START_DATE, EventResponse::getStartDate)
            .returns(NEW_END_DATE, EventResponse::getEndDate)
            .returns(EventRequestFactory.NEW_TIME, EventResponse::getTime)
            .returns(EventRequestFactory.NEW_TITLE, EventResponse::getTitle)
            .returns(EventRequestFactory.NEW_CONTENT, EventResponse::getContent)
            .returns(List.of(), EventResponse::getLabels);

        List<LocalDate> occurrences = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessTokenId, eventId)
            .stream()
            .map(OccurrenceResponse::getDate)
            .toList();

        assertThat(occurrences).hasSize(4);

        assertThat(occurrences).containsExactlyInAnyOrder(
            NEW_START_DATE.with(DayOfWeek.WEDNESDAY),
            NEW_START_DATE.with(DayOfWeek.SUNDAY),
            NEW_START_DATE.plusWeeks(1).with(DayOfWeek.WEDNESDAY),
            NEW_START_DATE.plusWeeks(1).with(DayOfWeek.SUNDAY)
        );
    }

    private UUID create(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_WEEK)
            .toBuilder()
            .startDate(START_DATE)
            .endDate(END_DATE)
            .build();

        UUID eventId = CalendarEventActions.createEvent(getServerPort(), accessTokenId, request);

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId))
            .returns(eventId, EventResponse::getEventId)
            .returns(RepetitionType.DAYS_OF_WEEK, EventResponse::getRepetitionType)
            .returns(EventRequestFactory.DEFAULT_REPETITION_DATA_DAYS_OF_WEEK, EventResponse::getRepetitionData)
            .returns(1, EventResponse::getRepeatForDays)
            .returns(START_DATE, EventResponse::getStartDate)
            .returns(END_DATE, EventResponse::getEndDate)
            .returns(EventRequestFactory.DEFAULT_TIME, EventResponse::getTime)
            .returns(EventRequestFactory.DEFAULT_TITLE, EventResponse::getTitle)
            .returns(EventRequestFactory.DEFAULT_CONTENT, EventResponse::getContent)
            .returns(0, EventResponse::getRemindMeBeforeDays)
            .returns(List.of(), EventResponse::getLabels);

        List<LocalDate> occurrences = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessTokenId, eventId)
            .stream()
            .map(OccurrenceResponse::getDate)
            .toList();

        assertThat(occurrences).hasSize(4);

        assertThat(occurrences).containsExactlyInAnyOrder(
            START_DATE.with(DayOfWeek.TUESDAY),
            START_DATE.with(DayOfWeek.SATURDAY),
            START_DATE.plusWeeks(1).with(DayOfWeek.TUESDAY),
            START_DATE.plusWeeks(1).with(DayOfWeek.SATURDAY)
        );

        return eventId;
    }
}
