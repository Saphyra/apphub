package com.github.saphyra.apphub.integraton.backend.diary;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.diary.DiarySearchActions;
import com.github.saphyra.apphub.integration.action.backend.diary.EventActions;
import com.github.saphyra.apphub.integration.action.backend.diary.OccurrenceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.diary.CalendarResponse;
import com.github.saphyra.apphub.integration.structure.diary.CreateEventRequest;
import com.github.saphyra.apphub.integration.structure.diary.EditOccurrenceRequest;
import com.github.saphyra.apphub.integration.structure.diary.EventSearchResponse;
import com.github.saphyra.apphub.integration.structure.diary.OccurrenceSearchResponse;
import com.github.saphyra.apphub.integration.structure.diary.ReferenceDate;
import com.github.saphyra.apphub.integration.structure.diary.RepetitionType;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
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

    @Test(dataProvider = "languageDataProvider", groups = "diary")
    public void searchInDiary(Language language) {
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

        //Query too short
        Response queryTooShortResponse = DiarySearchActions.getSearchResponse(language, accessTokenId, "as");

        ResponseValidator.verifyInvalidParam(language, queryTooShortResponse, "value", "too short");

        //Search by event
        List<EventSearchResponse> searchResult = DiarySearchActions.search(language, accessTokenId, TITLE);

        verifySearchResult(searchResult);

        //Search by occurrence
        searchResult = DiarySearchActions.search(language, accessTokenId, NOTE);

        verifySearchResult(searchResult);

        //Search by date
        searchResult = DiarySearchActions.search(language, accessTokenId, EVENT_DATE.toString());

        verifySearchResult(searchResult);

        //Search by time
        searchResult = DiarySearchActions.search(language, accessTokenId, EVENT_TIME.toString());

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
        assertThat(occurrenceSearchResponse.getStatus()).isEqualTo(Constants.DIARY_OCCURRENCE_STATUS_PENDING);
        assertThat(occurrenceSearchResponse.getNote()).isEqualTo(NOTE);
    }
}
