package com.github.saphyra.apphub.integration.backend.calendar;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
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

    @Test(groups = {"be", "calendar"})
    public void createEvent_validation() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        nullRepeat(accessTokenId);
        repeatTooLow(accessTokenId);
        repeatTooHigh(accessTokenId);
        blankTitle(accessTokenId);
        nullReferenceDate(accessTokenId);
        nullReferenceDateMonth(accessTokenId);
        nullReferenceDateDay(accessTokenId);
        nullDate(accessTokenId);
        nullRepetitionType(accessTokenId);
        emptyRepetitionDaysOfWeek(accessTokenId);
        zeroRepetitionDays(accessTokenId);
        nullMinutes(accessTokenId);
        minutesTooLow(accessTokenId);
        minutesTooHigh(accessTokenId);
        nullHours(accessTokenId);
        hoursTooLow(accessTokenId);
        hoursTooHigh(accessTokenId);
    }

    private static void nullRepeat(UUID accessTokenId) {
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

        Response nullRepeatResponse = EventActions.getCreateEventResponse(getServerPort(), accessTokenId, nullRepeatRequest);

        ResponseValidator.verifyInvalidParam(nullRepeatResponse, "repeat", "must not be null");
    }

    private static void repeatTooLow(UUID accessTokenId) {
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

        Response repeatTooLowResponse = EventActions.getCreateEventResponse(getServerPort(), accessTokenId, repeatTooLowRequest);

        ResponseValidator.verifyInvalidParam(repeatTooLowResponse, "repeat", "too low");
    }

    private static void repeatTooHigh(UUID accessTokenId) {
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

        Response repeatTooHighResponse = EventActions.getCreateEventResponse(getServerPort(), accessTokenId, repeatTooHighRequest);

        ResponseValidator.verifyInvalidParam(repeatTooHighResponse, "repeat", "too high");
    }

    private static void blankTitle(UUID accessTokenId) {
        CreateEventRequest blankTitleRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(" ")
            .repetitionType(RepetitionType.ONE_TIME)
            .build();

        Response blankTitleResponse = EventActions.getCreateEventResponse(getServerPort(), accessTokenId, blankTitleRequest);

        ResponseValidator.verifyInvalidParam(blankTitleResponse, "title", "must not be null or blank");
    }

    private static void nullReferenceDate(UUID accessTokenId) {
        CreateEventRequest nullReferenceDateRequest = CreateEventRequest.builder()
            .referenceDate(null)
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(RepetitionType.ONE_TIME)
            .build();

        Response nullReferenceDateResponse = EventActions.getCreateEventResponse(getServerPort(), accessTokenId, nullReferenceDateRequest);

        ResponseValidator.verifyInvalidParam(nullReferenceDateResponse, "referenceDate", "must not be null");
    }

    private static void nullReferenceDateMonth(UUID accessTokenId) {
        CreateEventRequest nullReferenceDateMonthRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(null)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(RepetitionType.ONE_TIME)
            .build();

        Response nullReferenceDateMonthResponse = EventActions.getCreateEventResponse(getServerPort(), accessTokenId, nullReferenceDateMonthRequest);

        ResponseValidator.verifyInvalidParam(nullReferenceDateMonthResponse, "referenceDate.month", "must not be null");
    }

    private static void nullReferenceDateDay(UUID accessTokenId) {
        CreateEventRequest nullReferenceDateDayRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(null)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(RepetitionType.ONE_TIME)
            .build();

        Response nullReferenceDateDayhResponse = EventActions.getCreateEventResponse(getServerPort(), accessTokenId, nullReferenceDateDayRequest);

        ResponseValidator.verifyInvalidParam(nullReferenceDateDayhResponse, "referenceDate.day", "must not be null");
    }

    private static void nullDate(UUID accessTokenId) {
        CreateEventRequest nullDateRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(null)
            .title(TITLE)
            .repetitionType(RepetitionType.ONE_TIME)
            .build();

        Response nullDateResponse = EventActions.getCreateEventResponse(getServerPort(), accessTokenId, nullDateRequest);

        ResponseValidator.verifyInvalidParam(nullDateResponse, "date", "must not be null");
    }

    private static void nullRepetitionType(UUID accessTokenId) {
        CreateEventRequest nullRepetitionTypeRequest = CreateEventRequest.builder()
            .referenceDate(ReferenceDate.builder()
                .day(REFERENCE_DATE_DAY)
                .month(REFERENCE_DATE_MONTH)
                .build())
            .date(EVENT_DATE)
            .title(TITLE)
            .repetitionType(null)
            .build();

        Response nullRepetitionTypeResponse = EventActions.getCreateEventResponse(getServerPort(), accessTokenId, nullRepetitionTypeRequest);

        ResponseValidator.verifyInvalidParam(nullRepetitionTypeResponse, "repetitionType", "must not be null");
    }

    private static void emptyRepetitionDaysOfWeek(UUID accessTokenId) {
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

        Response emptyRepetitionTypeDaysOfWeekResponse = EventActions.getCreateEventResponse(getServerPort(), accessTokenId, emptyRepetitionTypeDaysOfWeekRequest);

        ResponseValidator.verifyInvalidParam(emptyRepetitionTypeDaysOfWeekResponse, "repetitionDaysOfWeek", "must not be empty");
    }

    private static void zeroRepetitionDays(UUID accessTokenId) {
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

        Response zeroRepetitionTypeDaysResponse = EventActions.getCreateEventResponse(getServerPort(), accessTokenId, zeroRepetitionTypeDaysRequest);

        ResponseValidator.verifyInvalidParam(zeroRepetitionTypeDaysResponse, "repetitionDays", "too low");
    }

    private static void nullMinutes(UUID accessTokenId) {
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

        Response nullMinutesResponse = EventActions.getCreateEventResponse(getServerPort(), accessTokenId, nullMinutesRequest);

        ResponseValidator.verifyInvalidParam(nullMinutesResponse, "minutes", "must not be null");
    }

    private static void minutesTooLow(UUID accessTokenId) {
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

        Response minutesTooLowResponse = EventActions.getCreateEventResponse(getServerPort(), accessTokenId, minutesTooLowRequest);

        ResponseValidator.verifyInvalidParam(minutesTooLowResponse, "minutes", "too low");
    }

    private static void minutesTooHigh(UUID accessTokenId) {
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

        Response minutesTooHighResponse = EventActions.getCreateEventResponse(getServerPort(), accessTokenId, minutesTooHighRequest);

        ResponseValidator.verifyInvalidParam(minutesTooHighResponse, "minutes", "too high");
    }

    private static void nullHours(UUID accessTokenId) {
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

        Response nullHoursResponse = EventActions.getCreateEventResponse(getServerPort(), accessTokenId, nullHoursRequest);

        ResponseValidator.verifyInvalidParam(nullHoursResponse, "hours", "must not be null");
    }

    private static void hoursTooLow(UUID accessTokenId) {
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

        Response hoursTooLowResponse = EventActions.getCreateEventResponse(getServerPort(), accessTokenId, hoursTooLowRequest);

        ResponseValidator.verifyInvalidParam(hoursTooLowResponse, "hours", "too low");
    }

    private static void hoursTooHigh(UUID accessTokenId) {
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

        Response hoursTooHighResponse = EventActions.getCreateEventResponse(getServerPort(), accessTokenId, hoursTooHighRequest);

        ResponseValidator.verifyInvalidParam(hoursTooHighResponse, "hours", "too high");
    }

    @Test(groups = {"be", "calendar"})
    public void createOneTimeEvent() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

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

        List<CalendarResponse> responses = EventActions.createEvent(getServerPort(), accessTokenId, request);

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
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

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

        List<CalendarResponse> responses = EventActions.createEvent(getServerPort(), accessTokenId, request);

        assertThat(responses.stream().flatMap(calendarResponse -> calendarResponse.getEvents().stream()).findAny()).isNotEmpty();

        verifyDatabaseIntegrity(responses, userData.getEmail());
    }

    @Test(groups = {"be", "calendar"})
    public void createDaysOfMonthEvent() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

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

        List<CalendarResponse> responses = EventActions.createEvent(getServerPort(), accessTokenId, request);

        assertThat(responses.stream().flatMap(calendarResponse -> calendarResponse.getEvents().stream()).findAny()).isNotEmpty();

        verifyDatabaseIntegrity(responses, userData.getEmail());
    }

    @Test(groups = {"be", "calendar"})
    public void createEveryXDaysEvent() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

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

        List<CalendarResponse> responses = EventActions.createEvent(getServerPort(), accessTokenId, request);

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
