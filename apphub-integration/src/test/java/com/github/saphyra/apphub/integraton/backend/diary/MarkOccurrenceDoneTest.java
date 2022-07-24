package com.github.saphyra.apphub.integraton.backend.diary;

import com.github.saphyra.apphub.integration.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.diary.EventActions;
import com.github.saphyra.apphub.integration.action.backend.diary.OccurrenceActions;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.diary.CalendarResponse;
import com.github.saphyra.apphub.integration.structure.diary.CreateEventRequest;
import com.github.saphyra.apphub.integration.structure.diary.OccurrenceResponse;
import com.github.saphyra.apphub.integration.structure.diary.ReferenceDate;
import com.github.saphyra.apphub.integration.structure.diary.RepetitionType;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
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
    private static final LocalDate EVENT_DATE = CURRENT_DATE.minusWeeks(1);
    private static final String TITLE = "title";

    @Test(dataProvider = "languageDataProvider", groups = "diary")
    public void markOccurrenceDone(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateEventRequest request = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(RepetitionType.ONE_TIME)
            .build();

        List<CalendarResponse> responses = EventActions.createEvent(language, accessTokenId, request);

        CalendarResponse calendarResponse = responses.stream()
            .filter(cr -> !cr.getEvents().isEmpty())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("CalendarResponse not found"));
        OccurrenceResponse occurrenceResponse = calendarResponse.getEvents()
            .get(0);

        //Null referenceDate day
        ReferenceDate nullReferenceDateDay = ReferenceDate.builder()
            .day(null)
            .month(REFERENCE_DATE_MONTH)
            .build();

        Response nullReferenceDateDayResponse = OccurrenceActions.getMarkOccurrenceDoneResponse(language, accessTokenId, occurrenceResponse.getOccurrenceId(), nullReferenceDateDay);

        ResponseValidator.verifyInvalidParam(language, nullReferenceDateDayResponse, "referenceDate.day", "must not be null");

        //Null referenceDate month
        ReferenceDate nullReferenceDateMonth = ReferenceDate.builder()
            .day(REFERENCE_DATE_DAY)
            .month(null)
            .build();

        Response nullReferenceDateMonthResponse = OccurrenceActions.getMarkOccurrenceDoneResponse(language, accessTokenId, occurrenceResponse.getOccurrenceId(), nullReferenceDateMonth);

        ResponseValidator.verifyInvalidParam(language, nullReferenceDateMonthResponse, "referenceDate.month", "must not be null");

        //Mark occurrence done
        ReferenceDate referenceDate = ReferenceDate.builder()
            .day(REFERENCE_DATE_DAY)
            .month(REFERENCE_DATE_MONTH)
            .build();

        responses = OccurrenceActions.markOccurrenceDone(language, accessTokenId, occurrenceResponse.getOccurrenceId(), referenceDate);

        OccurrenceResponse occurrence = responses.stream()
            .flatMap(cr -> cr.getEvents().stream())
            .filter(or -> or.getOccurrenceId().equals(occurrenceResponse.getOccurrenceId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Occurrence not found"));

        assertThat(occurrence.getStatus()).isEqualTo(Constants.DIARY_OCCURRENCE_STATUS_DONE);
    }
}
