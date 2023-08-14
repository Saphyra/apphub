package com.github.saphyra.apphub.integraton.backend.calendar;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.calendar.CalendarResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.CreateEventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.Occurrence;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.ReferenceDate;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class EventCrudTest extends BackEndTest {
    private static final LocalDate CURRENT_DATE = LocalDate.now(ZoneOffset.UTC);
    private static final LocalDate REFERENCE_DATE_DAY = CURRENT_DATE.plusMonths(1);
    private static final LocalDate REFERENCE_DATE_MONTH = CURRENT_DATE;
    private static final LocalDate EVENT_DATE = CURRENT_DATE.minusWeeks(2);
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    public static final int REPETITION_DAYS = 3;
    private static final Integer HOURS = 23;
    private static final Integer MINUTES = 24;

    @Test(dataProvider = "languageDataProvider", groups = {"be", "calendar"})
    public void createEvent_validation(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        //Null repeat
        CreateEventRequest nullRepeatRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(RepetitionType.ONE_TIME)
            .repeat(null)
            .build();

        Response nullRepeatResponse = EventActions.getCreateEventResponse(language, accessTokenId, nullRepeatRequest);

        ResponseValidator.verifyInvalidParam(language, nullRepeatResponse, "repeat", "must not be null");

        //Repeat too low
        CreateEventRequest repeatTooLowRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(RepetitionType.ONE_TIME)
            .repeat(0)
            .build();

        Response repeatTooLowResponse = EventActions.getCreateEventResponse(language, accessTokenId, repeatTooLowRequest);

        ResponseValidator.verifyInvalidParam(language, repeatTooLowResponse, "repeat", "too low");

        //Repeat too high
        CreateEventRequest repeatTooHighRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(RepetitionType.ONE_TIME)
            .repeat(366)
            .build();

        Response repeatTooHighResponse = EventActions.getCreateEventResponse(language, accessTokenId, repeatTooHighRequest);

        ResponseValidator.verifyInvalidParam(language, repeatTooHighResponse, "repeat", "too high");

        //Blank title
        CreateEventRequest blankTitleRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(" ")
            .repetitionType(RepetitionType.ONE_TIME)
            .build();

        Response blankTitleResponse = EventActions.getCreateEventResponse(language, accessTokenId, blankTitleRequest);

        ResponseValidator.verifyInvalidParam(language, blankTitleResponse, "title", "must not be null or blank");

        //Null referenceDate
        CreateEventRequest nullReferenceDateRequest = CreateEventRequest.builder()
            .referenceDate(null)
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(RepetitionType.ONE_TIME)
            .build();

        Response nullReferenceDateResponse = EventActions.getCreateEventResponse(language, accessTokenId, nullReferenceDateRequest);

        ResponseValidator.verifyInvalidParam(language, nullReferenceDateResponse, "referenceDate", "must not be null");

        //Null referenceDate month
        CreateEventRequest nullReferenceDateMonthRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(null)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(RepetitionType.ONE_TIME)
            .build();

        Response nullReferenceDateMonthResponse = EventActions.getCreateEventResponse(language, accessTokenId, nullReferenceDateMonthRequest);

        ResponseValidator.verifyInvalidParam(language, nullReferenceDateMonthResponse, "referenceDate.month", "must not be null");

        //Null referenceDate day
        CreateEventRequest nullReferenceDateDayRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(null)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(RepetitionType.ONE_TIME)
            .build();

        Response nullReferenceDateDayhResponse = EventActions.getCreateEventResponse(language, accessTokenId, nullReferenceDateDayRequest);

        ResponseValidator.verifyInvalidParam(language, nullReferenceDateDayhResponse, "referenceDate.day", "must not be null");

        //Null date
        CreateEventRequest nullDateRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(null)
            .title(TITLE)
            .repetitionType(RepetitionType.ONE_TIME)
            .build();

        Response nullDateResponse = EventActions.getCreateEventResponse(language, accessTokenId, nullDateRequest);

        ResponseValidator.verifyInvalidParam(language, nullDateResponse, "date", "must not be null");

        //Null repetitionType
        CreateEventRequest nullRepetitionTypeRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(null)
            .build();

        Response nullRepetitionTypeResponse = EventActions.getCreateEventResponse(language, accessTokenId, nullRepetitionTypeRequest);

        ResponseValidator.verifyInvalidParam(language, nullRepetitionTypeResponse, "repetitionType", "must not be null");

        //Empty repetitionTypeDaysOfWeek
        CreateEventRequest emptyRepetitionTypeDaysOfWeekRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(RepetitionType.DAYS_OF_WEEK)
            .repetitionDaysOfWeek(Collections.emptyList())
            .build();

        Response emptyRepetitionTypeDaysOfWeekResponse = EventActions.getCreateEventResponse(language, accessTokenId, emptyRepetitionTypeDaysOfWeekRequest);

        ResponseValidator.verifyInvalidParam(language, emptyRepetitionTypeDaysOfWeekResponse, "repetitionDaysOfWeek", "must not be empty");

        //Zero repetitionTypeDays
        CreateEventRequest zeroRepetitionTypeDaysRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(RepetitionType.EVERY_X_DAYS)
            .repetitionDays(0)
            .build();

        Response zeroRepetitionTypeDaysResponse = EventActions.getCreateEventResponse(language, accessTokenId, zeroRepetitionTypeDaysRequest);

        ResponseValidator.verifyInvalidParam(language, zeroRepetitionTypeDaysResponse, "repetitionDays", "too low");

        //Null minutes
        CreateEventRequest nullMinutesRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(RepetitionType.ONE_TIME)
            .hours(1)
            .minutes(null)
            .build();

        Response nullMinutesResponse = EventActions.getCreateEventResponse(language, accessTokenId, nullMinutesRequest);

        ResponseValidator.verifyInvalidParam(language, nullMinutesResponse, "minutes", "must not be null");

        //Minutes too low
        CreateEventRequest minutesTooLowRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(RepetitionType.ONE_TIME)
            .hours(1)
            .minutes(-1)
            .build();

        Response minutesTooLowResponse = EventActions.getCreateEventResponse(language, accessTokenId, minutesTooLowRequest);

        ResponseValidator.verifyInvalidParam(language, minutesTooLowResponse, "minutes", "too low");

        //Minutes too low
        CreateEventRequest minutesTooHighRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(RepetitionType.ONE_TIME)
            .hours(1)
            .minutes(60)
            .build();

        Response minutesTooHighResponse = EventActions.getCreateEventResponse(language, accessTokenId, minutesTooHighRequest);

        ResponseValidator.verifyInvalidParam(language, minutesTooHighResponse, "minutes", "too high");

        //Null hours
        CreateEventRequest nullHoursRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(RepetitionType.ONE_TIME)
            .hours(null)
            .minutes(1)
            .build();

        Response nullHoursResponse = EventActions.getCreateEventResponse(language, accessTokenId, nullHoursRequest);

        ResponseValidator.verifyInvalidParam(language, nullHoursResponse, "hours", "must not be null");

        //Hours too low
        CreateEventRequest hoursTooLowRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(RepetitionType.ONE_TIME)
            .hours(-1)
            .minutes(1)
            .build();

        Response hoursTooLowResponse = EventActions.getCreateEventResponse(language, accessTokenId, hoursTooLowRequest);

        ResponseValidator.verifyInvalidParam(language, hoursTooLowResponse, "hours", "too low");

        //Hours too low
        CreateEventRequest hoursTooHighRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(RepetitionType.ONE_TIME)
            .hours(24)
            .minutes(1)
            .build();

        Response hoursTooHighResponse = EventActions.getCreateEventResponse(language, accessTokenId, hoursTooHighRequest);

        ResponseValidator.verifyInvalidParam(language, hoursTooHighResponse, "hours", "too high");
    }

    @Test(groups = {"be", "calendar"})
    public void createOneTimeEvent() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateEventRequest request = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(EVENT_DATE)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .content(CONTENT)
            .repetitionType(RepetitionType.ONE_TIME)
            .hours(HOURS)
            .minutes(MINUTES)
            .build();

        List<CalendarResponse> responses = EventActions.createEvent(language, accessTokenId, request);

        responses.forEach(calendarResponse -> {
            if (calendarResponse.getDate().equals(EVENT_DATE)) {
                assertThat(calendarResponse.getEvents()).hasSize(1);
                OccurrenceResponse occurrenceResponse = calendarResponse.getEvents().get(0);
                assertThat(occurrenceResponse.getOccurrenceId()).isNotNull();
                assertThat(occurrenceResponse.getEventId()).isNotNull();
                assertThat(occurrenceResponse.getStatus()).isEqualTo(Constants.CALENDAR_OCCURRENCE_STATUS_EXPIRED);
                assertThat(occurrenceResponse.getTitle()).isEqualTo(TITLE);
                assertThat(occurrenceResponse.getContent()).isEqualTo(CONTENT);
                assertThat(occurrenceResponse.getNote()).isNull();
                assertThat(occurrenceResponse.getTime()).isEqualTo(LocalTime.of(HOURS, MINUTES, 0));

            } else if (calendarResponse.getDate().equals(CURRENT_DATE)) {
                assertThat(calendarResponse.getEvents()).hasSize(1);
                OccurrenceResponse occurrenceResponse = calendarResponse.getEvents().get(0);
                assertThat(occurrenceResponse.getOccurrenceId()).isNotNull();
                assertThat(occurrenceResponse.getEventId()).isNotNull();
                assertThat(occurrenceResponse.getStatus()).isEqualTo(Constants.CALENDAR_OCCURRENCE_STATUS_EXPIRED);
                assertThat(occurrenceResponse.getTitle()).isEqualTo(TITLE);
                assertThat(occurrenceResponse.getContent()).isEqualTo(CONTENT);
                assertThat(occurrenceResponse.getNote()).isNull();
                assertThat(occurrenceResponse.getTime()).isEqualTo(LocalTime.of(HOURS, MINUTES, 0));
            } else {
                assertThat(calendarResponse.getEvents()).isEmpty();
            }
        });

        verifyDatabaseIntegrity(responses, userData.getEmail());
    }

    @Test(groups = {"be", "calendar"})
    public void createDaysOfWeekEvent() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        List<DayOfWeek> daysOfWeek = List.of(DayOfWeek.MONDAY, DayOfWeek.THURSDAY);
        CreateEventRequest request = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .content(CONTENT)
            .repetitionType(RepetitionType.DAYS_OF_WEEK)
            .repetitionDaysOfWeek(daysOfWeek)
            .build();

        List<CalendarResponse> responses = EventActions.createEvent(language, accessTokenId, request);

        assertThat(responses.stream().flatMap(calendarResponse -> calendarResponse.getEvents().stream()).findAny()).isNotEmpty();

        verifyDatabaseIntegrity(responses, userData.getEmail());
    }

    @Test(groups = {"be", "calendar"})
    public void createDaysOfMonthEvent() {
        Language language = Language.HUNGARIAN;
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
            .repetitionType(RepetitionType.DAYS_OF_MONTH)
            .repetitionDaysOfMonth(List.of(REFERENCE_DATE_DAY.getDayOfMonth()))
            .build();

        List<CalendarResponse> responses = EventActions.createEvent(language, accessTokenId, request);

        assertThat(responses.stream().flatMap(calendarResponse -> calendarResponse.getEvents().stream()).findAny()).isNotEmpty();

        verifyDatabaseIntegrity(responses, userData.getEmail());
    }

    @Test(groups = {"be", "calendar"})
    public void createEveryXDaysEvent() {
        Language language = Language.HUNGARIAN;
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

        assertThat(responses.stream().flatMap(calendarResponse -> calendarResponse.getEvents().stream()).findAny()).isNotEmpty();

        verifyDatabaseIntegrity(responses, userData.getEmail());
    }

    private void verifyDatabaseIntegrity(List<CalendarResponse> responses, String email) {
        List<UUID> responseIds = responses.stream()
            .filter(calendarResponse -> !calendarResponse.getEvents().isEmpty())
            .flatMap(calendarResponse -> calendarResponse.getEvents().stream())
            .map(OccurrenceResponse::getOccurrenceId)
            .collect(Collectors.toList());

        List<UUID> databaseIds = DatabaseUtil.getCalendarOccurrencesByUserId(DatabaseUtil.getUserIdByEmail(email))
            .stream()
            .map(Occurrence::getOccurrenceId)
            .collect(Collectors.toList());

        assertThat(databaseIds).containsAll(responseIds);
    }
}
