package com.github.saphyra.apphub.integraton.backend.diary;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.diary.EventActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.diary.CalendarResponse;
import com.github.saphyra.apphub.integration.structure.diary.CreateEventRequest;
import com.github.saphyra.apphub.integration.structure.diary.OccurrenceResponse;
import com.github.saphyra.apphub.integration.structure.diary.ReferenceDate;
import com.github.saphyra.apphub.integration.structure.diary.RepetitionType;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

public class RepeatedEventTest extends BackEndTest {
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalDate REFERENCE_DATE_MONTH = CURRENT_DATE;
    private static final LocalDate FIRST_OF_MONTH = LocalDate.of(CURRENT_DATE.getYear(), CURRENT_DATE.getMonth(), 1);
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final Integer REPETITION_DAYS = 5;

    @Test(groups = "diary")
    public void oneTimeEvent() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateEventRequest request = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(FIRST_OF_MONTH)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(FIRST_OF_MONTH)
            .title(TITLE)
            .content(CONTENT)
            .repetitionType(RepetitionType.ONE_TIME)
            .repeat(3)
            .build();

        List<CalendarResponse> responses = EventActions.createEvent(language, accessTokenId, request);

        assertThat(findByDate(responses, FIRST_OF_MONTH)).hasSize(1);
        assertThat(findByDate(responses, FIRST_OF_MONTH.plusDays(1))).isNotEmpty();
        assertThat(findByDate(responses, FIRST_OF_MONTH.plusDays(2))).isNotEmpty();
    }

    @Test(groups = "diary")
    public void daysOfWeekEvent() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        List<DayOfWeek> daysOfWeek = List.of(DayOfWeek.MONDAY, DayOfWeek.THURSDAY);
        LocalDate date = getFirstOfMonth(FIRST_OF_MONTH, d -> d.getDayOfWeek() == DayOfWeek.MONDAY);
        CreateEventRequest request = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(FIRST_OF_MONTH)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(date)
            .title(TITLE)
            .content(CONTENT)
            .repetitionType(RepetitionType.DAYS_OF_WEEK)
            .repetitionDaysOfWeek(daysOfWeek)
            .repeat(2)
            .build();

        List<CalendarResponse> responses = EventActions.createEvent(language, accessTokenId, request);

        assertThat(findByDate(responses, date)).hasSize(1);
        assertThat(findByDate(responses, date.plusDays(1))).isNotEmpty();

        LocalDate firstThursday = getFirstOfMonth(date, d -> d.getDayOfWeek() == DayOfWeek.THURSDAY);

        assertThat(findByDate(responses, firstThursday)).isNotEmpty();
        assertThat(findByDate(responses, firstThursday.plusDays(1))).isNotEmpty();
    }

    @Test(groups = "diary")
    public void daysOfMonthEvent() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateEventRequest request = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(FIRST_OF_MONTH)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(FIRST_OF_MONTH)
            .title(TITLE)
            .content(CONTENT)
            .repetitionType(RepetitionType.DAYS_OF_MONTH)
            .repetitionDaysOfMonth(List.of(1, 10, 15))
            .repeat(3)
            .build();

        List<CalendarResponse> responses = EventActions.createEvent(language, accessTokenId, request);

        assertThat(findByDate(responses, FIRST_OF_MONTH)).hasSize(1);
        assertThat(findByDate(responses, FIRST_OF_MONTH.plusDays(1))).isNotEmpty();
        assertThat(findByDate(responses, FIRST_OF_MONTH.plusDays(2))).isNotEmpty();

        LocalDate date = LocalDate.of(CURRENT_DATE.getYear(), CURRENT_DATE.getMonth(), 10);
        assertThat(findByDate(responses, date)).isNotEmpty();
        assertThat(findByDate(responses, date.plusDays(1))).isNotEmpty();
        assertThat(findByDate(responses, date.plusDays(2))).isNotEmpty();

        date = LocalDate.of(CURRENT_DATE.getYear(), CURRENT_DATE.getMonth(), 15);
        assertThat(findByDate(responses, date)).isNotEmpty();
        assertThat(findByDate(responses, date.plusDays(1))).isNotEmpty();
        assertThat(findByDate(responses, date.plusDays(2))).isNotEmpty();
    }

    @Test(groups = "diary")
    public void everyXDaysEvent() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateEventRequest request = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(FIRST_OF_MONTH)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(FIRST_OF_MONTH)
            .title(TITLE)
            .content(CONTENT)
            .repetitionType(RepetitionType.EVERY_X_DAYS)
            .repetitionDays(REPETITION_DAYS)
            .repeat(3)
            .build();

        List<CalendarResponse> responses = EventActions.createEvent(language, accessTokenId, request);

        assertThat(findByDate(responses, FIRST_OF_MONTH)).hasSize(1);
        assertThat(findByDate(responses, FIRST_OF_MONTH.plusDays(1))).isNotEmpty();
        assertThat(findByDate(responses, FIRST_OF_MONTH.plusDays(2))).isNotEmpty();

        LocalDate date = FIRST_OF_MONTH.plusDays(5);
        assertThat(findByDate(responses, date)).isNotEmpty();
        assertThat(findByDate(responses, date.plusDays(1))).isNotEmpty();
        assertThat(findByDate(responses, date.plusDays(2))).isNotEmpty();

    }

    private List<OccurrenceResponse> findByDate(List<CalendarResponse> responses, LocalDate date) {
        return responses.stream()
            .filter(calendarResponse -> calendarResponse.getDate().equals(date))
            .map(CalendarResponse::getEvents)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No response found for date " + date));
    }

    private LocalDate getFirstOfMonth(LocalDate startDate, Predicate<LocalDate> predicate) {
        for (int day = startDate.getDayOfMonth(); day < 28; day++) {
            LocalDate date = LocalDate.of(CURRENT_DATE.getYear(), CURRENT_DATE.getMonth(), day);

            if (predicate.test(date)) {
                return date;
            }
        }

        throw new RuntimeException("No matching date in month.");
    }
}
