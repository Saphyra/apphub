package com.github.saphyra.apphub.integration.backend.calendar;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.OccurrenceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
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

public class MarkOccurrenceDoneTest extends BackEndTest {
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalDate REFERENCE_DATE_DAY = CURRENT_DATE.plusMonths(1);
    private static final LocalDate REFERENCE_DATE_MONTH = CURRENT_DATE;
    private static final String TITLE = "title";

    @Test(groups = {"be", "calendar"})
    public void markOccurrenceDone() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        CreateEventRequest request = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(REFERENCE_DATE_DAY)
            .title(TITLE)
            .repetitionType(RepetitionType.ONE_TIME)
            .build();

        List<CalendarResponse> responses = EventActions.createEvent(getServerPort(), accessTokenId, request);

        CalendarResponse calendarResponse = responses.stream()
            .filter(cr -> !cr.getEvents().isEmpty())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("CalendarResponse not found"));
        OccurrenceResponse occurrenceResponse = calendarResponse.getEvents()
            .get(0);

        nullReferenceDateDay(accessTokenId, occurrenceResponse);
        nullReferenceDateMonth(accessTokenId, occurrenceResponse);
        markOccurrenceDone(accessTokenId, occurrenceResponse);
    }

    private static void nullReferenceDateDay(UUID accessTokenId, OccurrenceResponse occurrenceResponse) {
        ReferenceDate nullReferenceDateDay = ReferenceDate.builder()
            .day(null)
            .month(REFERENCE_DATE_MONTH)
            .build();

        Response nullReferenceDateDayResponse = OccurrenceActions.getMarkOccurrenceDoneResponse(getServerPort(), accessTokenId, occurrenceResponse.getOccurrenceId(), nullReferenceDateDay);

        ResponseValidator.verifyInvalidParam(nullReferenceDateDayResponse, "referenceDate.day", "must not be null");
    }

    private static void nullReferenceDateMonth(UUID accessTokenId, OccurrenceResponse occurrenceResponse) {
        ReferenceDate nullReferenceDateMonth = ReferenceDate.builder()
            .day(REFERENCE_DATE_DAY)
            .month(null)
            .build();

        Response nullReferenceDateMonthResponse = OccurrenceActions.getMarkOccurrenceDoneResponse(getServerPort(), accessTokenId, occurrenceResponse.getOccurrenceId(), nullReferenceDateMonth);

        ResponseValidator.verifyInvalidParam(nullReferenceDateMonthResponse, "referenceDate.month", "must not be null");
    }

    private static void markOccurrenceDone(UUID accessTokenId, OccurrenceResponse occurrenceResponse) {
        List<CalendarResponse> responses;
        ReferenceDate referenceDate = ReferenceDate.builder()
            .day(REFERENCE_DATE_DAY)
            .month(REFERENCE_DATE_MONTH)
            .build();

        responses = OccurrenceActions.markOccurrenceDone(getServerPort(), accessTokenId, occurrenceResponse.getOccurrenceId(), referenceDate);

        OccurrenceResponse occurrence = responses.stream()
            .flatMap(cr -> cr.getEvents().stream())
            .filter(or -> or.getOccurrenceId().equals(occurrenceResponse.getOccurrenceId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Occurrence not found"));

        assertThat(occurrence.getStatus()).isEqualTo(Constants.CALENDAR_OCCURRENCE_STATUS_DONE);
    }
}
