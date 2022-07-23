package com.github.saphyra.apphub.integration.action.backend.diary;

import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.diary.CalendarResponse;
import com.github.saphyra.apphub.integration.structure.diary.EditOccurrenceRequest;
import com.github.saphyra.apphub.integration.structure.diary.ReferenceDate;
import io.restassured.response.Response;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OccurrenceActions {
    public static List<CalendarResponse> editOccurrence(Language language, UUID accessTokenId, UUID occurrenceId, EditOccurrenceRequest request) {
        Response response = getEditOccurrenceResponse(language, accessTokenId, occurrenceId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return CollectionUtils.toList(response.getBody().as(CalendarResponse[].class));
    }

    public static Response getEditOccurrenceResponse(Language language, UUID accessTokenId, UUID occurrenceId, EditOccurrenceRequest request) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(request)
            .post(UrlFactory.create(Endpoints.DIARY_OCCURRENCE_EDIT, "occurrenceId", occurrenceId));
    }

    public static List<CalendarResponse> markOccurrenceDone(Language language, UUID accessTokenId, UUID occurrenceId, ReferenceDate referenceDate) {
        Response response = getMarkOccurrenceDoneResponse(language, accessTokenId, occurrenceId, referenceDate);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return CollectionUtils.toList(response.getBody().as(CalendarResponse[].class));
    }

    public static Response getMarkOccurrenceDoneResponse(Language language, UUID accessTokenId, UUID occurrenceId, ReferenceDate referenceDate) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(referenceDate)
            .post(UrlFactory.create(Endpoints.DIARY_OCCURRENCE_DONE, "occurrenceId", occurrenceId));
    }

    public static List<CalendarResponse> markOccurrenceSnoozed(Language language, UUID accessTokenId, UUID occurrenceId, ReferenceDate referenceDate) {
        Response response = getMarkOccurrenceSnoozedResponse(language, accessTokenId, occurrenceId, referenceDate);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return CollectionUtils.toList(response.getBody().as(CalendarResponse[].class));
    }

    public static Response getMarkOccurrenceSnoozedResponse(Language language, UUID accessTokenId, UUID occurrenceId, ReferenceDate referenceDate) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(referenceDate)
            .post(UrlFactory.create(Endpoints.DIARY_OCCURRENCE_SNOOZED, "occurrenceId", occurrenceId));
    }

    public static List<CalendarResponse> markOccurrenceDefault(Language language, UUID accessTokenId, UUID occurrenceId, ReferenceDate referenceDate) {
        Response response = getMarkOccurrenceDefaultResponse(language, accessTokenId, occurrenceId, referenceDate);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return CollectionUtils.toList(response.getBody().as(CalendarResponse[].class));
    }

    public static Response getMarkOccurrenceDefaultResponse(Language language, UUID accessTokenId, UUID occurrenceId, ReferenceDate referenceDate) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(referenceDate)
            .post(UrlFactory.create(Endpoints.DIARY_OCCURRENCE_DEFAULT, "occurrenceId", occurrenceId));
    }
}
