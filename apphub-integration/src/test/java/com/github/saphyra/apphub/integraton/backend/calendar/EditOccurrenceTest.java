package com.github.saphyra.apphub.integraton.backend.calendar;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.OccurrenceActions;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.calendar.CalendarResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.CreateEventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.EditOccurrenceRequest;
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

public class EditOccurrenceTest extends BackEndTest {
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalDate REFERENCE_DATE_DAY = CURRENT_DATE.plusMonths(1);
    private static final LocalDate REFERENCE_DATE_MONTH = CURRENT_DATE;
    private static final LocalDate EVENT_DATE = CURRENT_DATE.minusWeeks(1);
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    public static final int REPETITION_DAYS = 3;
    private static final String NEW_TITLE = "new-title";
    private static final String NEW_CONTENT = "new-content";
    private static final String NOTE = "NOTE";

    @Test(dataProvider = "languageDataProvider", groups = {"be", "calendar"})
    public void editOccurrence(Language language) {
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

        CalendarResponse calendarResponse = responses.stream()
            .filter(cr -> !cr.getEvents().isEmpty())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("CalendarResponse not found"));
        OccurrenceResponse occurrenceResponse = calendarResponse.getEvents()
            .get(0);

        nullReferenceDate(language, accessTokenId, occurrenceResponse);
        nullReferenceDateDay(language, accessTokenId, occurrenceResponse);
        nullReferenceDateMonth(language, accessTokenId, occurrenceResponse);
        blankTitle(language, accessTokenId, occurrenceResponse);
        editOccurrence(language, accessTokenId, occurrenceResponse);
    }

    private static void nullReferenceDate(Language language, UUID accessTokenId, OccurrenceResponse occurrenceResponse) {
        EditOccurrenceRequest nullReferenceDateRequest = EditOccurrenceRequest.builder()
            .referenceDate(null)
            .title(NEW_TITLE)
            .content(NEW_CONTENT)
            .note(NOTE)
            .build();

        Response nullReferenceDateResponse = OccurrenceActions.getEditOccurrenceResponse(language, accessTokenId, occurrenceResponse.getOccurrenceId(), nullReferenceDateRequest);

        ResponseValidator.verifyInvalidParam(language, nullReferenceDateResponse, "referenceDate", "must not be null");
    }

    private static void nullReferenceDateDay(Language language, UUID accessTokenId, OccurrenceResponse occurrenceResponse) {
        EditOccurrenceRequest nullReferenceDateDayRequest = EditOccurrenceRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(null)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .title(NEW_TITLE)
            .content(NEW_CONTENT)
            .note(NOTE)
            .build();

        Response nullReferenceDateDayResponse = OccurrenceActions.getEditOccurrenceResponse(language, accessTokenId, occurrenceResponse.getOccurrenceId(), nullReferenceDateDayRequest);

        ResponseValidator.verifyInvalidParam(language, nullReferenceDateDayResponse, "referenceDate.day", "must not be null");
    }

    private static void nullReferenceDateMonth(Language language, UUID accessTokenId, OccurrenceResponse occurrenceResponse) {
        EditOccurrenceRequest nullReferenceDateMonthRequest = EditOccurrenceRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(null)
                .build())
            .title(NEW_TITLE)
            .content(NEW_CONTENT)
            .note(NOTE)
            .build();

        Response nullReferenceDateMonthResponse = OccurrenceActions.getEditOccurrenceResponse(language, accessTokenId, occurrenceResponse.getOccurrenceId(), nullReferenceDateMonthRequest);

        ResponseValidator.verifyInvalidParam(language, nullReferenceDateMonthResponse, "referenceDate.month", "must not be null");
    }

    private static void blankTitle(Language language, UUID accessTokenId, OccurrenceResponse occurrenceResponse) {
        EditOccurrenceRequest blankTitleRequest = EditOccurrenceRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .title(" ")
            .content(NEW_CONTENT)
            .note(NOTE)
            .build();

        Response blankTitleResponse = OccurrenceActions.getEditOccurrenceResponse(language, accessTokenId, occurrenceResponse.getOccurrenceId(), blankTitleRequest);

        ResponseValidator.verifyInvalidParam(language, blankTitleResponse, "title", "must not be null or blank");
    }

    private static void editOccurrence(Language language, UUID accessTokenId, OccurrenceResponse occurrenceResponse) {
        List<CalendarResponse> responses;
        EditOccurrenceRequest editOccurrenceRequest = EditOccurrenceRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .title(NEW_TITLE)
            .content(NEW_CONTENT)
            .note(NOTE)
            .build();

        responses = OccurrenceActions.editOccurrence(language, accessTokenId, occurrenceResponse.getOccurrenceId(), editOccurrenceRequest);

        responses.stream()
            .filter(cr -> !cr.getEvents().isEmpty())
            .forEach(cr -> {
                OccurrenceResponse occurrence = cr.getEvents()
                    .get(0);

                assertThat(occurrence.getTitle()).isEqualTo(NEW_TITLE);
                assertThat(occurrence.getContent()).isEqualTo(NEW_CONTENT);
                if (occurrence.getOccurrenceId().equals(occurrenceResponse.getOccurrenceId())) {
                    assertThat(occurrence.getNote()).isEqualTo(NOTE);
                } else {
                    assertThat(occurrence.getNote()).isNull();
                }
            });
    }
}
