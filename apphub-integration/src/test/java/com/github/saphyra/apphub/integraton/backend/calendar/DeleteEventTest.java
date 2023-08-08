package com.github.saphyra.apphub.integraton.backend.calendar;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventActions;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.calendar.CalendarResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.CreateEventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.ReferenceDate;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteEventTest extends BackEndTest {
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalDate REFERENCE_DATE_DAY = CURRENT_DATE.plusMonths(1);
    private static final LocalDate REFERENCE_DATE_MONTH = CURRENT_DATE;
    private static final LocalDate EVENT_DATE = CURRENT_DATE.minusWeeks(1);
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    public static final int REPETITION_DAYS = 3;

    @Test(dataProvider = "languageDataProvider")
    public void deleteEvent(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateEventRequest request = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .content(CONTENT)
            .repetitionType(RepetitionType.EVERY_X_DAYS)
            .repetitionDays(REPETITION_DAYS)
            .build();

        List<CalendarResponse> responses = EventActions.createEvent(language, accessTokenId, request);

        UUID eventId = responses.stream()
            .filter(calendarResponse -> !calendarResponse.getEvents().isEmpty())
            .findFirst()
            .map(CalendarResponse::getEvents)
            .map(occurrenceResponses -> occurrenceResponses.get(0))
            .map(OccurrenceResponse::getEventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        //Null referenceDateDay
        ReferenceDate nullReferenceDateDay = ReferenceDate.builder()
            .day(null)
            .month(REFERENCE_DATE_MONTH)
            .build();

        Response nullReferenceDateDayResponse = EventActions.getDeleteEventResponse(language, accessTokenId, eventId, nullReferenceDateDay);

        ResponseValidator.verifyInvalidParam(language, nullReferenceDateDayResponse, "referenceDate.day", "must not be null");

        //Null referenceDateMonth
        ReferenceDate nullReferenceDateMonth = ReferenceDate.builder()
            .day(REFERENCE_DATE_DAY)
            .month(null)
            .build();

        Response nullReferenceDateMonthResponse = EventActions.getDeleteEventResponse(language, accessTokenId, eventId, nullReferenceDateMonth);

        ResponseValidator.verifyInvalidParam(language, nullReferenceDateMonthResponse, "referenceDate.month", "must not be null");

        //Delete event
        ReferenceDate referenceDate = ReferenceDate.builder()
            .day(REFERENCE_DATE_DAY)
            .month(REFERENCE_DATE_MONTH)
            .build();

        List<CalendarResponse> deleteEventResponse = EventActions.deleteEvent(language, accessTokenId, eventId, referenceDate);

        deleteEventResponse.forEach(calendarResponse -> assertThat(calendarResponse.getEvents()).isEmpty());
    }
}
