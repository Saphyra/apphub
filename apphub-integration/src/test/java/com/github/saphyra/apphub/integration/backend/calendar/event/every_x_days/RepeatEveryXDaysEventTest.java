package com.github.saphyra.apphub.integration.backend.calendar.event.every_x_days;

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

public class RepeatEveryXDaysEventTest extends BackEndTest {
    private static final Integer REPEAT_FOR_DAYS = 3;
    private static final LocalDate END_DATE = EventRequestFactory.DEFAULT_START_DATE.plusDays(EventRequestFactory.DEFAULT_REPETITION_DATA_EVERY_X_DAYS);
    private static final LocalDate NEW_END_DATE = EventRequestFactory.NEW_START_DATE.plusDays(EventRequestFactory.NEW_REPETITION_DATA_EVERY_X_DAYS);
    private static final int NEW_REPEAT_FOR_DAYS = 2;

    @Test(groups = {"be", "calendar"})
    public void repeatEveryXDaysEvent() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID eventId = create(accessTokenId);
        edit(accessTokenId, eventId);
    }

    private void edit(UUID accessTokenId, UUID eventId) {
        EventRequest request = EventRequestFactory.editRequest(RepetitionType.EVERY_X_DAYS)
            .toBuilder()
            .endDate(NEW_END_DATE)
            .repeatForDays(NEW_REPEAT_FOR_DAYS)
            .build();

        CalendarEventActions.editEvent(getServerPort(), accessTokenId, eventId, request);

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId).getRepeatForDays()).isEqualTo(NEW_REPEAT_FOR_DAYS);

        Map<LocalDate, OccurrenceResponse> occurrences = CalendarOccurrenceActions.getOccurrences(
                getServerPort()
                , accessTokenId,
                EventRequestFactory.DEFAULT_START_DATE.minusDays(6),
                EventRequestFactory.DEFAULT_END_DATE
            )
            .stream()
            .collect(Collectors.toMap(OccurrenceResponse::getDate, o -> o));

        assertThat(occurrences).hasSize(4);

        assertThat(occurrences.get(EventRequestFactory.NEW_START_DATE))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(EventRequestFactory.NEW_START_DATE.plusDays(1)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(EventRequestFactory.NEW_START_DATE.plusDays(EventRequestFactory.NEW_REPETITION_DATA_EVERY_X_DAYS)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(EventRequestFactory.NEW_START_DATE.plusDays(EventRequestFactory.NEW_REPETITION_DATA_EVERY_X_DAYS + 1)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);
    }

    private UUID create(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.EVERY_X_DAYS)
            .toBuilder()
            .repeatForDays(REPEAT_FOR_DAYS)
            .endDate(END_DATE)
            .build();

        UUID eventId = CalendarEventActions.createEvent(getServerPort(), accessTokenId, request);

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId).getRepeatForDays()).isEqualTo(REPEAT_FOR_DAYS);

        Map<LocalDate, OccurrenceResponse> occurrences = CalendarOccurrenceActions.getOccurrences(
                getServerPort(),
                accessTokenId,
                EventRequestFactory.DEFAULT_START_DATE.minusDays(6),
                EventRequestFactory.DEFAULT_END_DATE
            )
            .stream()
            .collect(Collectors.toMap(OccurrenceResponse::getDate, o -> o));

        assertThat(occurrences).hasSize(6);

        assertThat(occurrences.get(EventRequestFactory.DEFAULT_START_DATE))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(EventRequestFactory.DEFAULT_START_DATE.plusDays(1)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(EventRequestFactory.DEFAULT_START_DATE.plusDays(2)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(EventRequestFactory.DEFAULT_START_DATE.plusDays(EventRequestFactory.DEFAULT_REPETITION_DATA_EVERY_X_DAYS)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(EventRequestFactory.DEFAULT_START_DATE.plusDays(EventRequestFactory.DEFAULT_REPETITION_DATA_EVERY_X_DAYS + 1)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);
        assertThat(occurrences.get(EventRequestFactory.DEFAULT_START_DATE.plusDays(EventRequestFactory.DEFAULT_REPETITION_DATA_EVERY_X_DAYS + 2)))
            .returns(OccurrenceStatus.PENDING, OccurrenceResponse::getStatus);

        return eventId;
    }
}
