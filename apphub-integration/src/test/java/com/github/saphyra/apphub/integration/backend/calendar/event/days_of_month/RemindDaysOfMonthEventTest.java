package com.github.saphyra.apphub.integration.backend.calendar.event.days_of_month;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarOccurrenceActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceStatus;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class RemindDaysOfMonthEventTest extends BackEndTest {
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
    private static final int REMIND_ME_BEFORE_DAYS = 1;
    private static final Integer NEW_REMIND_ME_BEFORE_DAYS = 2;

    @Test(groups = {"be", "calendar"})
    public void remindDaysOfMonthEvent() {
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
            .remindMeBeforeDays(NEW_REMIND_ME_BEFORE_DAYS)
            .build();

        CalendarEventActions.editEvent(getServerPort(), accessTokenId, eventId, request);

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId).getRemindMeBeforeDays()).isEqualTo(NEW_REMIND_ME_BEFORE_DAYS);

        Map<LocalDate, OccurrenceResponse> occurrences = CalendarOccurrenceActions.getOccurrences(
                getServerPort(),
                accessTokenId,
                NEW_START_DATE.minusMonths(2),
                NEW_END_DATE.plusMonths(2)
            )
            .stream()
            .collect(Collectors.toMap(OccurrenceResponse::getDate, o -> o));

        assertThat(occurrences).hasSize(8);

        assertThat(occurrences.get(NEW_START_DATE.withDayOfMonth(2).minusDays(NEW_REMIND_ME_BEFORE_DAYS)))
            .returns(OccurrenceStatus.REMINDER, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(NEW_START_DATE.withDayOfMonth(2)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);

        assertThat(occurrences.get(NEW_START_DATE.withDayOfMonth(24).minusDays(NEW_REMIND_ME_BEFORE_DAYS)))
            .returns(OccurrenceStatus.REMINDER, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(NEW_START_DATE.withDayOfMonth(24)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);

        assertThat(occurrences.get(NEW_START_DATE.plusMonths(1).withDayOfMonth(2).minusDays(NEW_REMIND_ME_BEFORE_DAYS)))
            .returns(OccurrenceStatus.REMINDER, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(NEW_START_DATE.plusMonths(1).withDayOfMonth(2)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);

        assertThat(occurrences.get(NEW_START_DATE.plusMonths(1).withDayOfMonth(24).minusDays(NEW_REMIND_ME_BEFORE_DAYS)))
            .returns(OccurrenceStatus.REMINDER, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(NEW_START_DATE.plusMonths(1).withDayOfMonth(24)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);
    }

    private UUID create(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .startDate(START_DATE)
            .endDate(END_DATE)
            .remindMeBeforeDays(REMIND_ME_BEFORE_DAYS)
            .build();

        UUID eventId = CalendarEventActions.createEvent(getServerPort(), accessTokenId, request);

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId).getRemindMeBeforeDays()).isEqualTo(REMIND_ME_BEFORE_DAYS);

        Map<LocalDate, OccurrenceResponse> occurrences = CalendarOccurrenceActions.getOccurrences(
                getServerPort(),
                accessTokenId,
                START_DATE.minusMonths(2),
                END_DATE.plusMonths(2)
            )
            .stream()
            .collect(Collectors.toMap(OccurrenceResponse::getDate, o -> o));

        assertThat(occurrences).hasSize(8);

        assertThat(occurrences.get(START_DATE.withDayOfMonth(7).minusDays(REMIND_ME_BEFORE_DAYS)))
            .returns(OccurrenceStatus.REMINDER, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(START_DATE.withDayOfMonth(7)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);

        assertThat(occurrences.get(START_DATE.withDayOfMonth(22).minusDays(REMIND_ME_BEFORE_DAYS)))
            .returns(OccurrenceStatus.REMINDER, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(START_DATE.withDayOfMonth(22)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);

        assertThat(occurrences.get(START_DATE.plusMonths(1).withDayOfMonth(7).minusDays(REMIND_ME_BEFORE_DAYS)))
            .returns(OccurrenceStatus.REMINDER, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(START_DATE.plusMonths(1).withDayOfMonth(7)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);

        assertThat(occurrences.get(START_DATE.plusMonths(1).withDayOfMonth(22).minusDays(REMIND_ME_BEFORE_DAYS)))
            .returns(OccurrenceStatus.REMINDER, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(START_DATE.plusMonths(1).withDayOfMonth(22)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);

        return eventId;
    }
}
