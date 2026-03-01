package com.github.saphyra.apphub.integration.backend.calendar.event;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpiredEventTest extends BackEndTest {
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalDate START_DATE = CURRENT_DATE.minusDays(3);
    private static final LocalDate END_DATE = CURRENT_DATE.plusDays(3);

    @Test(groups = {"be", "calendar"})
    void hideExpiredEvent() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID eventId = createExpiredEvent(accessTokenId);

        CustomAssertions.singleListAssertThat(CalendarEventActions.getExpiredEvents(getServerPort(), accessTokenId))
            .returns(eventId, EventResponse::getEventId);

        CalendarEventActions.hideExpiredEvent(getServerPort(), accessTokenId, eventId);

        assertThat(CalendarEventActions.getExpiredEvents(getServerPort(), accessTokenId)).isEmpty();
    }

    @Test(groups = {"be", "calendar"})
    void extendExpiredEvent() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID eventId = createExpiredEvent(accessTokenId);

        CustomAssertions.singleListAssertThat(CalendarEventActions.getExpiredEvents(getServerPort(), accessTokenId))
            .returns(eventId, EventResponse::getEventId);

        nullExtendUntil(accessTokenId, eventId);
        eventDurationTooLong(accessTokenId, eventId);
        extendExpiredEvent(accessTokenId, eventId);
    }

    private void eventDurationTooLong(UUID accessTokenId, UUID eventId) {
        ResponseValidator.verifyInvalidParam(CalendarEventActions.getExtendExpiredEventResponse(getServerPort(), accessTokenId, eventId, CURRENT_DATE.plusYears(3)), "eventDuration", "too long");
    }

    private void nullExtendUntil(UUID accessTokenId, UUID eventId) {
        ResponseValidator.verifyInvalidParam(CalendarEventActions.getExtendExpiredEventResponse(getServerPort(), accessTokenId, eventId, null), "endDate", "must not be null");
    }

    private static void extendExpiredEvent(UUID accessTokenId, UUID eventId) {
        CalendarEventActions.extendExpiredEvent(getServerPort(), accessTokenId, eventId, END_DATE.plusWeeks(1));

        assertThat(CalendarEventActions.getExpiredEvents(getServerPort(), accessTokenId)).isEmpty();

        assertThat(CalendarEventActions.getEvent(getServerPort(), accessTokenId, eventId))
            .returns(END_DATE.plusWeeks(1), EventResponse::getEndDate);
    }

    private UUID createExpiredEvent(UUID accessTokenId) {
        EventRequest request = EventRequestFactory.validRequest(RepetitionType.DAYS_OF_WEEK)
            .toBuilder()
            .repetitionData(List.of(CURRENT_DATE.getDayOfWeek()))
            .startDate(START_DATE)
            .endDate(END_DATE)
            .build();

        return CalendarEventActions.createEvent(getServerPort(), accessTokenId, request);
    }
}
