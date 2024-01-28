package com.github.saphyra.apphub.integration.action.backend.calendar;

import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.calendar.CalendarResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.CreateEventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.ReferenceDate;
import io.restassured.response.Response;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class EventActions {
    public static List<CalendarResponse> createEvent(UUID accessTokenId, CreateEventRequest request) {
        Response response = getCreateEventResponse(accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return CollectionUtils.toList(response.getBody().as(CalendarResponse[].class));
    }

    public static Response getCreateEventResponse(UUID accessTokenId, CreateEventRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.CALENDAR_CREATE_EVENT));
    }

    public static List<CalendarResponse> deleteEvent(UUID accessTokenId, UUID eventId, ReferenceDate referenceDate) {
        Response response = getDeleteEventResponse(accessTokenId, eventId, referenceDate);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return CollectionUtils.toList(response.getBody().as(CalendarResponse[].class));
    }

    public static Response getDeleteEventResponse(UUID accessTokenId, UUID eventId, ReferenceDate referenceDate) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(referenceDate)
            .delete(UrlFactory.create(Endpoints.CALENDAR_EVENT_DELETE, "eventId", eventId));
    }
}
