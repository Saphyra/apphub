package com.github.saphyra.apphub.integration.action.backend.calendar;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.CalendarEndpoints;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarEventActions {
    public static Response getCreateEvemtResponse(int serverPort, UUID accessTokenId, EventRequest request) {
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
        Response response = getCreateEvemtResponse(serverPort, accessTokenId, request);

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
}
