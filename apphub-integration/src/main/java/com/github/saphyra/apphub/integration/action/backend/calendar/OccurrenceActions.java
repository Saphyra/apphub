package com.github.saphyra.apphub.integration.action.backend.calendar;

import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.CalendarEndpoints;
import com.github.saphyra.apphub.integration.structure.api.calendar.CalendarResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.EditOccurrenceRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.ReferenceDate;
import io.restassured.response.Response;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OccurrenceActions {
    public static List<CalendarResponse> editOccurrence(int serverPort, UUID accessTokenId, UUID occurrenceId, EditOccurrenceRequest request) {
        Response response = getEditOccurrenceResponse(serverPort, accessTokenId, occurrenceId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return CollectionUtils.toList(response.getBody().as(CalendarResponse[].class));
    }

    public static Response getEditOccurrenceResponse(int serverPort, UUID accessTokenId, UUID occurrenceId, EditOccurrenceRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_OCCURRENCE_EDIT, "occurrenceId", occurrenceId));
    }

    public static List<CalendarResponse> markOccurrenceDone(int serverPort, UUID accessTokenId, UUID occurrenceId, ReferenceDate referenceDate) {
        Response response = getMarkOccurrenceDoneResponse(serverPort, accessTokenId, occurrenceId, referenceDate);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return CollectionUtils.toList(response.getBody().as(CalendarResponse[].class));
    }

    public static Response getMarkOccurrenceDoneResponse(int serverPort, UUID accessTokenId, UUID occurrenceId, ReferenceDate referenceDate) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(referenceDate)
            .post(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_OCCURRENCE_DONE, "occurrenceId", occurrenceId));
    }

    public static List<CalendarResponse> markOccurrenceSnoozed(int serverPort, UUID accessTokenId, UUID occurrenceId, ReferenceDate referenceDate) {
        Response response = getMarkOccurrenceSnoozedResponse(serverPort, accessTokenId, occurrenceId, referenceDate);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return CollectionUtils.toList(response.getBody().as(CalendarResponse[].class));
    }

    public static Response getMarkOccurrenceSnoozedResponse(int serverPort, UUID accessTokenId, UUID occurrenceId, ReferenceDate referenceDate) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(referenceDate)
            .post(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_OCCURRENCE_SNOOZED, "occurrenceId", occurrenceId));
    }

    public static List<CalendarResponse> markOccurrenceDefault(int serverPort, UUID accessTokenId, UUID occurrenceId, ReferenceDate referenceDate) {
        Response response = getMarkOccurrenceDefaultResponse(serverPort, accessTokenId, occurrenceId, referenceDate);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return CollectionUtils.toList(response.getBody().as(CalendarResponse[].class));
    }

    public static Response getMarkOccurrenceDefaultResponse(int serverPort, UUID accessTokenId, UUID occurrenceId, ReferenceDate referenceDate) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(referenceDate)
            .post(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_OCCURRENCE_DEFAULT, "occurrenceId", occurrenceId));
    }
}
