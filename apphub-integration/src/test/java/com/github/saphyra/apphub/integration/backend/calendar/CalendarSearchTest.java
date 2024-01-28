package com.github.saphyra.apphub.integration.backend.calendar;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarSearchActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.OccurrenceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.calendar.CalendarResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.CreateEventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.EditOccurrenceRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventSearchResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceSearchResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.ReferenceDate;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarSearchTest extends BackEndTest {
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalDate REFERENCE_DATE_DAY = CURRENT_DATE.plusMonths(1);
    private static final LocalDate REFERENCE_DATE_MONTH = CURRENT_DATE;
    private static final LocalDate EVENT_DATE = CURRENT_DATE.plusDays(1);
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String NOTE = "note";
    private static final LocalTime EVENT_TIME = LocalTime.now()
        .withNano(0)
        .withSecond(0);

    @Test(groups = {"be", "calendar"})
    public void searchInCalendar() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        CreateEventRequest request = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .hours(EVENT_TIME.getHour())
            .minutes(EVENT_TIME.getMinute())
            .title(TITLE)
            .content(CONTENT)
            .repetitionType(RepetitionType.ONE_TIME)
            .build();
        List<CalendarResponse> calendar = EventActions.createEvent(accessTokenId, request);

        UUID occurrenceId = calendar.stream()
            .filter(calendarResponse -> !calendarResponse.getEvents().isEmpty())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Event was not created."))
            .getEvents()
            .get(0)
            .getOccurrenceId();

        EditOccurrenceRequest editOccurrenceRequest = EditOccurrenceRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .title(TITLE)
            .content(CONTENT)
            .note(NOTE)
            .build();
        OccurrenceActions.editOccurrence(accessTokenId, occurrenceId, editOccurrenceRequest);

        queryTooShort(accessTokenId);
        searchByEvent(accessTokenId);
        searchByOccurrence(accessTokenId);
        searchByDate(accessTokenId);
        searchByTime(accessTokenId);
    }

    private static void queryTooShort(UUID accessTokenId) {
        Response queryTooShortResponse = CalendarSearchActions.getSearchResponse(accessTokenId, "as");

        ResponseValidator.verifyInvalidParam(queryTooShortResponse, "value", "too short");
    }

    private void searchByEvent(UUID accessTokenId) {
        List<EventSearchResponse> searchResult = CalendarSearchActions.search(accessTokenId, TITLE);

        verifySearchResult(searchResult);
    }

    private void searchByOccurrence(UUID accessTokenId) {
        List<EventSearchResponse> searchResult;
        searchResult = CalendarSearchActions.search(accessTokenId, NOTE);

        verifySearchResult(searchResult);
    }

    private void searchByDate(UUID accessTokenId) {
        List<EventSearchResponse> searchResult;
        searchResult = CalendarSearchActions.search(accessTokenId, EVENT_DATE.toString());

        verifySearchResult(searchResult);
    }

    private void searchByTime(UUID accessTokenId) {
        List<EventSearchResponse> searchResult;
        searchResult = CalendarSearchActions.search(accessTokenId, EVENT_TIME.toString());

        verifySearchResult(searchResult);
    }

    private void verifySearchResult(List<EventSearchResponse> searchResult) {
        assertThat(searchResult).hasSize(1);
        EventSearchResponse eventSearchResponse = searchResult.get(0);
        assertThat(eventSearchResponse.getTime()).isEqualTo(String.format("%s %s", EVENT_DATE, EVENT_TIME));
        assertThat(eventSearchResponse.getRepetitionType()).isEqualTo(RepetitionType.ONE_TIME);
        assertThat(eventSearchResponse.getTitle()).isEqualTo(TITLE);
        assertThat(eventSearchResponse.getContent()).isEqualTo(CONTENT);

        assertThat(eventSearchResponse.getOccurrences()).hasSize(1);
        OccurrenceSearchResponse occurrenceSearchResponse = eventSearchResponse.getOccurrences()
            .get(0);
        assertThat(occurrenceSearchResponse.getDate()).isEqualTo(EVENT_DATE);
        assertThat(occurrenceSearchResponse.getTime()).isEqualTo(EVENT_TIME);
        assertThat(occurrenceSearchResponse.getStatus()).isEqualTo(Constants.CALENDAR_OCCURRENCE_STATUS_PENDING);
        assertThat(occurrenceSearchResponse.getNote()).isEqualTo(NOTE);
    }
}
