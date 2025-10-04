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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicEveryXDaysEventTest extends BackEndTest {
    private static final LocalDate NEW_END_DATE = EventRequestFactory.NEW_START_DATE.plusDays(7);
    private static final Integer NEW_REPEAT_FOR_DAYS = 2;
    private static final Integer NEW_REMIND_ME_BEFORE_DAYS = 1;

    @Test(groups = {"be", "calendar"})
    public void basicEveryXDaysEvent() {
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
            EventRequestFactory.DEFAULT_START_DATE.minusDays(10),
            EventRequestFactory.DEFAULT_END_DATE.plusDays(10)
        )).isEmpty();
    }

    private void edit(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.editRequest(RepetitionType.EVERY_X_DAYS)
            .toBuilder()
            .endDate(NEW_END_DATE)
            .repeatForDays(NEW_REPEAT_FOR_DAYS)
            .remindMeBeforeDays(NEW_REMIND_ME_BEFORE_DAYS)
            .build();

        CalendarEventActions.editEvent(getServerPort(), accessTokenId, eventId, request);

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId))
            .returns(eventId, EventResponse::getEventId)
            .returns(RepetitionType.EVERY_X_DAYS, EventResponse::getRepetitionType)
            .returns(EventRequestFactory.NEW_REPETITION_DATA_EVERY_X_DAYS, EventResponse::getRepetitionData)
            .returns(NEW_REPEAT_FOR_DAYS, EventResponse::getRepeatForDays)
            .returns(EventRequestFactory.NEW_START_DATE, EventResponse::getStartDate)
            .returns(NEW_END_DATE, EventResponse::getEndDate)
            .returns(EventRequestFactory.NEW_TIME, EventResponse::getTime)
            .returns(EventRequestFactory.NEW_TITLE, EventResponse::getTitle)
            .returns(EventRequestFactory.NEW_CONTENT, EventResponse::getContent)
            .returns(NEW_REMIND_ME_BEFORE_DAYS, EventResponse::getRemindMeBeforeDays)
            .returns(List.of(), EventResponse::getLabels);

        List<LocalDate> occurrences = CalendarOccurrenceActions.getOccurrencesOfEvent(getServerPort(), accessTokenId, eventId)
            .stream()
            .map(OccurrenceResponse::getDate)
            .toList();

        assertThat(occurrences).hasSize(4);

        assertThat(occurrences).containsExactlyInAnyOrder(
            EventRequestFactory.NEW_START_DATE,
            EventRequestFactory.NEW_START_DATE.plusDays(1),
            EventRequestFactory.NEW_START_DATE.plusDays(EventRequestFactory.NEW_REPETITION_DATA_EVERY_X_DAYS),
            EventRequestFactory.NEW_START_DATE.plusDays(EventRequestFactory.NEW_REPETITION_DATA_EVERY_X_DAYS + 1)
        );
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
