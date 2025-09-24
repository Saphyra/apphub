package com.github.saphyra.apphub.integration.backend.calendar.event.one_time;

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

public class RemindOneTimeEventTest extends BackEndTest {
    private static final Integer REMIND_ME_BEFORE_DAYS = 2;

    @Test(groups = {"be", "calendar"})
    public void remindOneTimeEvent() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID eventId = create(accessTokenId);
        edit(accessTokenId, eventId);
    }

    private void edit(UUID accessTokenId, UUID eventId) {
        int newRemindMeBeforeDays = EventRequestFactory.DEFAULT_FUTURE_DAYS + 1;

        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .remindMeBeforeDays(newRemindMeBeforeDays)
            .build();

        CalendarEventActions.editEvent(getServerPort(), accessTokenId, eventId, request);

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId).getRemindMeBeforeDays()).isEqualTo(newRemindMeBeforeDays);

        Map<LocalDate, OccurrenceResponse> occurrences = CalendarOccurrenceActions.getOccurrences(getServerPort(), accessTokenId, LocalDate.now(), EventRequestFactory.DEFAULT_START_DATE)
            .stream()
            .collect(Collectors.toMap(OccurrenceResponse::getDate, o -> o));

        assertThat(occurrences.get(LocalDate.now()))
            .returns(OccurrenceStatus.REMINDER, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(EventRequestFactory.DEFAULT_START_DATE))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);
    }

    private UUID create(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .remindMeBeforeDays(REMIND_ME_BEFORE_DAYS)
            .build();

        UUID eventId = CalendarEventActions.createEvent(getServerPort(), accessTokenId, request);

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId).getRemindMeBeforeDays()).isEqualTo(REMIND_ME_BEFORE_DAYS);

        Map<LocalDate, OccurrenceResponse> occurrences = CalendarOccurrenceActions.getOccurrences(getServerPort(), accessTokenId, LocalDate.now(), EventRequestFactory.DEFAULT_START_DATE)
            .stream()
            .collect(Collectors.toMap(OccurrenceResponse::getDate, o -> o));

        assertThat(occurrences.get(EventRequestFactory.DEFAULT_START_DATE.minusDays(REMIND_ME_BEFORE_DAYS)))
            .returns(OccurrenceStatus.REMINDER, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(EventRequestFactory.DEFAULT_START_DATE))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);

        return eventId;
    }
}
