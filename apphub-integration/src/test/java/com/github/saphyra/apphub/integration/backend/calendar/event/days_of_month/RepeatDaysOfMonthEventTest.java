package com.github.saphyra.apphub.integration.backend.calendar.event.days_of_month;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarOccurrenceActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class RepeatDaysOfMonthEventTest extends BackEndTest {
    private static final LocalDate START_DATE = LocalDate.now()
        .plusMonths(1)
        .withDayOfMonth(1);
    private static final LocalDate END_DATE = START_DATE.plusMonths(2)
        .withDayOfMonth(1)
        .minusDays(1);
    private static final LocalDate NEW_START_DATE = START_DATE.plusMonths(1);
    private static final LocalDate NEW_END_DATE = NEW_START_DATE.plusMonths(2)
        .withDayOfMonth(1)
        .minusDays(1);
    private static final Integer REPEAT_FOR_DAYS = 2;
    private static final Integer NEW_REPEAT_FOR_DAYS = 3;

    @Test(groups = {"be", "calendar"})
    public void repeatDaysOfMonthEvent() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID eventId = create(accessTokenId);
        edit(accessTokenId, eventId);
    }

    private void edit(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.editRequest(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .startDate(NEW_START_DATE)
            .endDate(NEW_END_DATE)
            .repeatForDays(NEW_REPEAT_FOR_DAYS)
            .build();

        CalendarEventActions.editEvent(getServerPort(), accessTokenId, eventId, request);

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId).getRepeatForDays()).isEqualTo(NEW_REPEAT_FOR_DAYS);

        List<LocalDate> occurrences = CalendarOccurrenceActions.getOccurrences(
                getServerPort(),
                accessTokenId,
                NEW_START_DATE.minusMonths(2),
                NEW_END_DATE.plusMonths(2)
            )
            .stream()
            .map(OccurrenceResponse::getDate)
            .toList();

        assertThat(occurrences).hasSize(12);

        assertThat(occurrences).containsExactlyInAnyOrder(
            NEW_START_DATE.withDayOfMonth(2),
            NEW_START_DATE.withDayOfMonth(3),
            NEW_START_DATE.withDayOfMonth(4),
            NEW_START_DATE.withDayOfMonth(24),
            NEW_START_DATE.withDayOfMonth(25),
            NEW_START_DATE.withDayOfMonth(26),

            NEW_START_DATE.plusMonths(1).withDayOfMonth(2),
            NEW_START_DATE.plusMonths(1).withDayOfMonth(3),
            NEW_START_DATE.plusMonths(1).withDayOfMonth(4),
            NEW_START_DATE.plusMonths(1).withDayOfMonth(24),
            NEW_START_DATE.plusMonths(1).withDayOfMonth(25),
            NEW_START_DATE.plusMonths(1).withDayOfMonth(26)
        );
    }

    private UUID create(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .startDate(START_DATE)
            .endDate(END_DATE)
            .repeatForDays(REPEAT_FOR_DAYS)
            .build();

        UUID eventId = CalendarEventActions.createEvent(getServerPort(), accessTokenId, request);

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId).getRepeatForDays()).isEqualTo(REPEAT_FOR_DAYS);

        List<LocalDate> occurrences = CalendarOccurrenceActions.getOccurrences(
                getServerPort(),
                accessTokenId,
                START_DATE.minusMonths(2),
                END_DATE.plusMonths(2)
            )
            .stream()
            .map(OccurrenceResponse::getDate)
            .toList();

        assertThat(occurrences).hasSize(8);

        assertThat(occurrences).containsExactlyInAnyOrder(
            START_DATE.withDayOfMonth(7),
            START_DATE.withDayOfMonth(8),
            START_DATE.withDayOfMonth(22),
            START_DATE.withDayOfMonth(23),

            START_DATE.plusMonths(1).withDayOfMonth(7),
            START_DATE.plusMonths(1).withDayOfMonth(8),
            START_DATE.plusMonths(1).withDayOfMonth(22),
            START_DATE.plusMonths(1).withDayOfMonth(23)
        );

        return eventId;
    }
}
