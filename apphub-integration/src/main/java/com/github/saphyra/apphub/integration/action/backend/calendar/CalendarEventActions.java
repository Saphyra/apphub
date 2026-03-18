package com.github.saphyra.apphub.integration.action.backend.calendar;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.CalendarEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventResponse;
import io.restassured.response.Response;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarEventActions {
    public static Response getCreateEventResponse(int serverPort, UUID accessTokenId, EventRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_CREATE_EVENT));
    }

    public static Response getGetEventsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_GET_EVENTS));
    }

    public static Response getGetEventResponse(int serverPort, UUID accessTokenId, UUID eventId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_GET_EVENT, "eventId", eventId));
    }

    public static Response getDeleteEventResponse(int serverPort, UUID accessTokenId, UUID eventId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_DELETE_EVENT, "eventId", eventId));
    }

    public static Response getEditEventResponse(int serverPort, UUID accessTokenId, UUID eventId, EventRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_EDIT_EVENT, "eventId", eventId));
    }

    public static UUID createEvent(int serverPort, UUID accessTokenId, EventRequest request) {
        Response response = getCreateEventResponse(serverPort, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody()
            .jsonPath()
            .getObject("value", UUID.class);
    }

    public static EventResponse getEvent(int serverPort, UUID accessTokenId, UUID eventId) {
        Response response = getGetEventResponse(serverPort, accessTokenId, eventId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(EventResponse.class);
    }

    public static void deleteEvent(int serverPort, UUID accessTokenId, UUID eventId) {
        Response response = getDeleteEventResponse(serverPort, accessTokenId, eventId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static List<EventResponse> getEvents(int serverPort, UUID accessTokenId) {
        Response response = getGetEventsResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(EventResponse[].class));
    }

    public static void editEvent(int serverPort, UUID accessTokenId, UUID eventId, EventRequest request) {
        Response response = getEditEventResponse(serverPort, accessTokenId, eventId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static List<EventResponse> getEvents(int serverPort, UUID accessTokenId, UUID labelId) {
        Response response = getGetEventsResponse(serverPort, accessTokenId, labelId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(EventResponse[].class));
    }

    private static Response getGetEventsResponse(int serverPort, UUID accessTokenId, UUID labelId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_GET_EVENTS, Map.of(), Map.of("labelId", labelId)));
    }

    public static Response getGetLabellessEventsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_GET_LABELLESS_EVENTS));
    }

    public static List<EventResponse> getEventsWithoutLabel(int serverPort, UUID accessTokenId) {
        Response response = getGetLabellessEventsResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(EventResponse[].class));
    }

    public static List<EventResponse> getExpiredEvents(int serverPort, UUID accessTokenId) {
        Response response  = getExpiredEventsResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(EventResponse[].class));
    }

    public static Response getExpiredEventsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_GET_EXPIRED_EVENTS));
    }

    public static void hideExpiredEvent(int serverPort, UUID accessTokenId, UUID eventId) {
        Response response = getHideExpiredEventResponse(serverPort, accessTokenId, eventId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getHideExpiredEventResponse(int serverPort, UUID accessTokenId, UUID eventId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_HIDE_EXPIRED_EVENT, "eventId", eventId));
    }

    public static void extendExpiredEvent(int serverPort, UUID accessTokenId, UUID eventId, LocalDate localDate) {
        Response response = getExtendExpiredEventResponse(serverPort, accessTokenId, eventId, localDate);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getExtendExpiredEventResponse(int serverPort, UUID accessTokenId, UUID eventId, LocalDate extendUntil) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(extendUntil))
            .post(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_EXTEND_EXPIRED_EVENT, "eventId", eventId));
    }

    public static Response getMergeEventsResponse(int serverPort, UUID accessTokenId, UUID eventId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_MERGE_EVENTS, "eventId", eventId));
    }

    public static void mergeEvents(int serverPort, UUID accessTokenId, UUID eventId) {
        Response response = getMergeEventsResponse(serverPort, accessTokenId, eventId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getSearchResponse(int serverPort, UUID accessTokenId, String searchText) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(searchText))
            .post(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_SEARCH_EVENTS));
    }

    public static List<EventResponse> search(int serverPort, UUID accessTokenId, String searchText) {
        Response response = getSearchResponse(serverPort, accessTokenId, searchText);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(EventResponse[].class));
    }

    public static Response getArchiveEventResponse(int serverPort, UUID accessTokenId, UUID eventId, Boolean archived) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(archived))
            .post(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_ARCHIVE_EVENT, "eventId", eventId));
    }

    public static void archiveEvent(int serverPort, UUID accessTokenId, UUID eventId, Boolean archived) {
        Response response = getArchiveEventResponse(serverPort, accessTokenId, eventId, archived);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
