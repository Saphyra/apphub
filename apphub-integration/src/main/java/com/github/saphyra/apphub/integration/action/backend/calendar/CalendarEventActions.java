package com.github.saphyra.apphub.integration.action.backend.calendar;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.CalendarEndpoints;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import io.restassured.response.Response;

import java.util.UUID;

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
}
