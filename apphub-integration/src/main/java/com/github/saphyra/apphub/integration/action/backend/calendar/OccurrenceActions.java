package com.github.saphyra.apphub.integration.action.backend.calendar;

import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.calendar.CalendarResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.EditOccurrenceRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.ReferenceDate;
import io.restassured.response.Response;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OccurrenceActions {
    public static List<CalendarResponse> editOccurrence(UUID accessTokenId, UUID occurrenceId, EditOccurrenceRequest request) {
        Response response = getEditOccurrenceResponse(accessTokenId, occurrenceId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return CollectionUtils.toList(response.getBody().as(CalendarResponse[].class));
    }

    public static Response getEditOccurrenceResponse(UUID accessTokenId, UUID occurrenceId, EditOccurrenceRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(Endpoints.CALENDAR_OCCURRENCE_EDIT, "occurrenceId", occurrenceId));
    }

    public static List<CalendarResponse> markOccurrenceDone(UUID accessTokenId, UUID occurrenceId, ReferenceDate referenceDate) {
        Response response = getMarkOccurrenceDoneResponse(accessTokenId, occurrenceId, referenceDate);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return CollectionUtils.toList(response.getBody().as(CalendarResponse[].class));
    }

    public static Response getMarkOccurrenceDoneResponse(UUID accessTokenId, UUID occurrenceId, ReferenceDate referenceDate) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(referenceDate)
            .post(UrlFactory.create(Endpoints.CALENDAR_OCCURRENCE_DONE, "occurrenceId", occurrenceId));
    }

    public static List<CalendarResponse> markOccurrenceSnoozed(UUID accessTokenId, UUID occurrenceId, ReferenceDate referenceDate) {
        Response response = getMarkOccurrenceSnoozedResponse(accessTokenId, occurrenceId, referenceDate);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return CollectionUtils.toList(response.getBody().as(CalendarResponse[].class));
    }

    public static Response getMarkOccurrenceSnoozedResponse(UUID accessTokenId, UUID occurrenceId, ReferenceDate referenceDate) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(referenceDate)
            .post(UrlFactory.create(Endpoints.CALENDAR_OCCURRENCE_SNOOZED, "occurrenceId", occurrenceId));
    }

    public static List<CalendarResponse> markOccurrenceDefault(UUID accessTokenId, UUID occurrenceId, ReferenceDate referenceDate) {
        Response response = getMarkOccurrenceDefaultResponse(accessTokenId, occurrenceId, referenceDate);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return CollectionUtils.toList(response.getBody().as(CalendarResponse[].class));
    }

    public static Response getMarkOccurrenceDefaultResponse(UUID accessTokenId, UUID occurrenceId, ReferenceDate referenceDate) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(referenceDate)
            .post(UrlFactory.create(Endpoints.CALENDAR_OCCURRENCE_DEFAULT, "occurrenceId", occurrenceId));
    }
}
