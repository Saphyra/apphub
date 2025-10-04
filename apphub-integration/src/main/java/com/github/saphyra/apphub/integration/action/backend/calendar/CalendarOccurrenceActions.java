package com.github.saphyra.apphub.integration.action.backend.calendar;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.CalendarEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceStatus;
import io.restassured.response.Response;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarOccurrenceActions {
    public static Response getCreateOccurrenceResponse(int serverPort, UUID accessTokenId, UUID eventId, OccurrenceRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_CREATE_OCCURRENCE, "eventId", eventId));
    }

    public static Response getEditOccurrenceResponse(int serverPort, UUID accessTokenId, UUID occurrenceId, OccurrenceRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_EDIT_OCCURRENCE, "occurrenceId", occurrenceId));
    }

    public static Response getDeleteOccurrenceResponse(int serverPort, UUID accessTokenId, UUID occurrenceId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_DELETE_OCCURRENCE, "occurrenceId", occurrenceId));
    }

    public static Response getGetOccurrencesResponse(int serverPort, UUID accessTokenId, LocalDate startDate, LocalDate endDate) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(
                serverPort,
                CalendarEndpoints.CALENDAR_GET_OCCURRENCES,
                Map.of(),
                Map.of(
                    "startDate", startDate,
                    "endDate", endDate
                )
            ));
    }

    public static Response getGetOccurrenceResponse(int serverPort, UUID accessTokenId, UUID occurrenceId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_GET_OCCURRENCE, "occurrenceId", occurrenceId));
    }

    public static Response getGetOccurrencesOfEventResponse(int serverPort, UUID accessTokenId, UUID eventId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_GET_OCCURRENCES_OF_EVENT, "eventId", eventId));
    }

    public static Response getEditOccurrenceStatusResponse(int serverPort, UUID accessTokenId, UUID occurrenceId, OccurrenceStatus status) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(status))
            .post(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_EDIT_OCCURRENCE_STATUS, "occurrenceId", occurrenceId));
    }

    public static Response getSetRemindedResponse(int serverPort, UUID accessTokenId, UUID occurrenceId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_OCCURRENCE_REMINDED, "occurrenceId", occurrenceId));
    }

    public static List<OccurrenceResponse> getOccurrencesOfEvent(int serverPort, UUID accessTokenId, UUID eventId) {
        Response response = getGetOccurrencesOfEventResponse(serverPort, accessTokenId, eventId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(OccurrenceResponse[].class));
    }

    public static List<OccurrenceResponse> getOccurrences(int serverPort, UUID accessTokenId, LocalDate startDate, LocalDate endDate) {
        Response response = getGetOccurrencesResponse(serverPort, accessTokenId, startDate, endDate);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(OccurrenceResponse[].class));
    }

    public static UUID createOccurrence(int serverPort, UUID accessTokenId, UUID eventId, OccurrenceRequest request) {
        Response response = getCreateOccurrenceResponse(serverPort, accessTokenId, eventId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.body().jsonPath().getObject("value", UUID.class);
    }

    public static void editOccurrence(int serverPort, UUID accessTokenId, UUID occurrenceId, OccurrenceRequest request) {
        Response response = getEditOccurrenceResponse(serverPort, accessTokenId, occurrenceId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static OccurrenceResponse editOccurrenceStatus(int serverPort, UUID accessTokenId, UUID occurrenceId, OccurrenceStatus status) {
        Response response = getEditOccurrenceStatusResponse(serverPort, accessTokenId, occurrenceId, status);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(OccurrenceResponse.class);
    }

    public static OccurrenceResponse setReminded(int serverPort, UUID accessTokenId, UUID occurrenceId) {
        Response response = getSetRemindedResponse(serverPort, accessTokenId, occurrenceId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(OccurrenceResponse.class);
    }
}
