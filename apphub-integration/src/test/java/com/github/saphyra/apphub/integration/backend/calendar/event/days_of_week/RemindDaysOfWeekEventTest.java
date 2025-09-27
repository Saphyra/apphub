package com.github.saphyra.apphub.integration.backend.calendar.event.days_of_week;

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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.framework.DateTimeUtil.nextMonday;
import static com.github.saphyra.apphub.integration.framework.DateTimeUtil.nextSunday;
import static org.assertj.core.api.Assertions.assertThat;

public class RemindDaysOfWeekEventTest extends BackEndTest {
    private static final LocalDate START_DATE = nextMonday();
    private static final LocalDate END_DATE = nextSunday(START_DATE);
    private static final LocalDate NEW_START_DATE = START_DATE.plusWeeks(1);
    private static final LocalDate NEW_END_DATE = nextSunday(NEW_START_DATE);
    private static final Integer REMIND_ME_BEFORE_DAYS = 2;

    @Test(groups = {"be", "calendar"})
    public void remindDaysOfWeekEvent() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID eventId = create(accessTokenId);
        edit(accessTokenId, eventId);
    }

    private void edit(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.editRequest(RepetitionType.DAYS_OF_WEEK)
            .toBuilder()
            .startDate(NEW_START_DATE)
            .endDate(NEW_END_DATE)
            .repeatForDays(1)
            .build();

        CalendarEventActions.editEvent(getServerPort(), accessTokenId, eventId, request);

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId).getRemindMeBeforeDays()).isEqualTo(EventRequestFactory.NEW_REMIND_ME_BEFORE_DAYS);

        Map<LocalDate, OccurrenceResponse> occurrences = CalendarOccurrenceActions.getOccurrences(
                getServerPort(),
                accessTokenId,
                NEW_START_DATE.minusDays(10),
                NEW_END_DATE.plusDays(10)
            )
            .stream()
            .collect(Collectors.toMap(OccurrenceResponse::getDate, o -> o));

        assertThat(occurrences).hasSize(8);

        assertThat(occurrences.get(NEW_START_DATE.with(DayOfWeek.WEDNESDAY).minusDays(EventRequestFactory.NEW_REMIND_ME_BEFORE_DAYS)))
            .returns(OccurrenceStatus.REMINDER, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(NEW_START_DATE.with(DayOfWeek.WEDNESDAY)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);

        assertThat(occurrences.get(NEW_START_DATE.with(DayOfWeek.SUNDAY).minusDays(EventRequestFactory.NEW_REMIND_ME_BEFORE_DAYS)))
            .returns(OccurrenceStatus.REMINDER, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(NEW_START_DATE.with(DayOfWeek.SUNDAY)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);

        assertThat(occurrences.get(NEW_START_DATE.plusWeeks(1).with(DayOfWeek.WEDNESDAY).minusDays(EventRequestFactory.NEW_REMIND_ME_BEFORE_DAYS)))
            .returns(OccurrenceStatus.REMINDER, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(NEW_START_DATE.plusWeeks(1).with(DayOfWeek.WEDNESDAY)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);

        assertThat(occurrences.get(NEW_START_DATE.plusWeeks(1).with(DayOfWeek.SUNDAY).minusDays(EventRequestFactory.NEW_REMIND_ME_BEFORE_DAYS)))
            .returns(OccurrenceStatus.REMINDER, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(NEW_START_DATE.plusWeeks(1).with(DayOfWeek.SUNDAY)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);
    }

    private UUID create(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_WEEK)
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
                START_DATE .minusDays(10),
                END_DATE.plusDays(10)
            )
            .stream()
            .collect(Collectors.toMap(OccurrenceResponse::getDate, o -> o));

        assertThat(occurrences).hasSize(8);

        assertThat(occurrences.get(START_DATE.with(DayOfWeek.TUESDAY).minusDays(REMIND_ME_BEFORE_DAYS)))
            .returns(OccurrenceStatus.REMINDER, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(START_DATE.with(DayOfWeek.TUESDAY)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);

        assertThat(occurrences.get(START_DATE.with(DayOfWeek.SATURDAY).minusDays(REMIND_ME_BEFORE_DAYS)))
            .returns(OccurrenceStatus.REMINDER, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(START_DATE.with(DayOfWeek.SATURDAY)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);

        assertThat(occurrences.get(START_DATE.plusWeeks(1).with(DayOfWeek.SATURDAY).minusDays(REMIND_ME_BEFORE_DAYS)))
            .returns(OccurrenceStatus.REMINDER, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(START_DATE.plusWeeks(1).with(DayOfWeek.SATURDAY)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);

        assertThat(occurrences.get(START_DATE.plusWeeks(1).with(DayOfWeek.SATURDAY).minusDays(REMIND_ME_BEFORE_DAYS)))
            .returns(OccurrenceStatus.REMINDER, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(START_DATE.plusWeeks(1).with(DayOfWeek.SATURDAY)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);

        return eventId;
    }
}
