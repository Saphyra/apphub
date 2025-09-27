package com.github.saphyra.apphub.integration.backend.calendar.event.days_of_week;

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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.DateTimeUtil.nextMonday;
import static com.github.saphyra.apphub.integration.framework.DateTimeUtil.nextSunday;
import static org.assertj.core.api.Assertions.assertThat;

public class RepeatDaysOfWeekEventTest extends BackEndTest {
    private static final LocalDate START_DATE = nextMonday();
    private static final LocalDate END_DATE = nextSunday(START_DATE);
    private static final LocalDate NEW_START_DATE = START_DATE.plusWeeks(1);
    private static final LocalDate NEW_END_DATE = nextSunday(NEW_START_DATE);
    private static final Integer REPEAT_FOR_DAYS = 3;

    @Test(groups = {"be", "calendar"})
    public void repeatDaysOfWeekEvent() {
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
            .remindMeBeforeDays(0)
            .build();

        CalendarEventActions.editEvent(getServerPort(), accessTokenId, eventId, request);

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId).getRepeatForDays()).isEqualTo(EventRequestFactory.NEW_REPEAT_FOR_DAYS);

        List<LocalDate> occurrences = CalendarOccurrenceActions.getOccurrences(
                getServerPort(),
                accessTokenId,
                START_DATE.minusDays(10),
                END_DATE.plusDays(10)
            )
            .stream()
            .map(OccurrenceResponse::getDate)
            .toList();

        assertThat(occurrences).hasSize(8);

        assertThat(occurrences).containsExactlyInAnyOrder(
            NEW_START_DATE.with(DayOfWeek.WEDNESDAY),
            NEW_START_DATE.with(DayOfWeek.WEDNESDAY).plusDays(1),
            NEW_START_DATE.with(DayOfWeek.SUNDAY),
            NEW_START_DATE.with(DayOfWeek.SUNDAY).plusDays(1),

            NEW_START_DATE.plusWeeks(1).with(DayOfWeek.WEDNESDAY),
            NEW_START_DATE.plusWeeks(1).with(DayOfWeek.WEDNESDAY).plusDays(1),
            NEW_START_DATE.plusWeeks(1).with(DayOfWeek.SUNDAY),
            NEW_START_DATE.plusWeeks(1).with(DayOfWeek.SUNDAY).plusDays(1)
        );
    }

    private UUID create(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_WEEK)
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
                START_DATE.minusDays(10),
                END_DATE.plusDays(10)
            )
            .stream()
            .map(OccurrenceResponse::getDate)
            .toList();

        assertThat(occurrences).hasSize(12);

        assertThat(occurrences).containsExactlyInAnyOrder(
            START_DATE.with(DayOfWeek.TUESDAY),
            START_DATE.with(DayOfWeek.TUESDAY).plusDays(1),
            START_DATE.with(DayOfWeek.TUESDAY).plusDays(2),
            START_DATE.with(DayOfWeek.SATURDAY),
            START_DATE.with(DayOfWeek.SATURDAY).plusDays(1),
            START_DATE.with(DayOfWeek.SATURDAY).plusDays(2),

            START_DATE.plusWeeks(1).with(DayOfWeek.TUESDAY),
            START_DATE.plusWeeks(1).with(DayOfWeek.TUESDAY).plusDays(1),
            START_DATE.plusWeeks(1).with(DayOfWeek.TUESDAY).plusDays(2),
            START_DATE.plusWeeks(1).with(DayOfWeek.SATURDAY),
            START_DATE.plusWeeks(1).with(DayOfWeek.SATURDAY).plusDays(1),
            START_DATE.plusWeeks(1).with(DayOfWeek.SATURDAY).plusDays(2)
        );

        return eventId;
    }
}
