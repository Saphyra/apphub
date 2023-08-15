package com.github.saphyra.apphub.integraton.backend.calendar;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarSearchActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.OccurrenceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
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
import java.time.LocalDateTime;
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

    @Test(dataProvider = "languageDataProvider", groups = {"be", "calendar"})
    public void searchInCalendar(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

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
        List<CalendarResponse> calendar = EventActions.createEvent(language, accessTokenId, request);

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
        OccurrenceActions.editOccurrence(language, accessTokenId, occurrenceId, editOccurrenceRequest);

        queryTooShort(language, accessTokenId);
        searchByEvent(language, accessTokenId);
        searchByOccurrence(language, accessTokenId);
        searchByDate(language, accessTokenId);
        searchByTime(language, accessTokenId);
    }

    private static void queryTooShort(Language language, UUID accessTokenId) {
        Response queryTooShortResponse = CalendarSearchActions.getSearchResponse(language, accessTokenId, "as");

        ResponseValidator.verifyInvalidParam(language, queryTooShortResponse, "value", "too short");
    }

    private void searchByEvent(Language language, UUID accessTokenId) {
        List<EventSearchResponse> searchResult = CalendarSearchActions.search(language, accessTokenId, TITLE);

        verifySearchResult(searchResult);
    }

    private void searchByOccurrence(Language language, UUID accessTokenId) {
        List<EventSearchResponse> searchResult;
        searchResult = CalendarSearchActions.search(language, accessTokenId, NOTE);

        verifySearchResult(searchResult);
    }

    private void searchByDate(Language language, UUID accessTokenId) {
        List<EventSearchResponse> searchResult;
        searchResult = CalendarSearchActions.search(language, accessTokenId, EVENT_DATE.toString());

        verifySearchResult(searchResult);
    }

    private void searchByTime(Language language, UUID accessTokenId) {
        List<EventSearchResponse> searchResult;
        searchResult = CalendarSearchActions.search(language, accessTokenId, EVENT_TIME.toString());

        verifySearchResult(searchResult);
    }

    private void verifySearchResult(List<EventSearchResponse> searchResult) {
        assertThat(searchResult).hasSize(1);
        EventSearchResponse eventSearchResponse = searchResult.get(0);
        assertThat(eventSearchResponse.getTime()).isEqualTo(LocalDateTime.of(EVENT_DATE, EVENT_TIME));
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
