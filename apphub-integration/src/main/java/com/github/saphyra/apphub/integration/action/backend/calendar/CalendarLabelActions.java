package com.github.saphyra.apphub.integration.action.backend.calendar;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.CalendarEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.LabelResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarLabelActions {
    public static Response getCreateLabelResponse(int serverPort, UUID accessTokenId, String label) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(label))
            .put(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_CREATE_LABEL));
    }

    public static UUID createLabel(int serverPort, UUID accessTokenId, String label) {
        Response response = getCreateLabelResponse(serverPort, accessTokenId, label);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody()
            .jsonPath()
            .getObject("value", UUID.class);
    }

    public static List<LabelResponse> getLabels(int serverPort, UUID accessTokenId) {
        Response response = getGetLabelsResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(LabelResponse[].class));
    }

    public static Response getGetLabelsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_GET_LABELS));
    }

    public static LabelResponse getLabel(int serverPort, UUID accessTokenId, UUID labelId) {
        Response response = getGetLabelResponse(serverPort, accessTokenId, labelId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(LabelResponse.class);
    }

    public static Response getGetLabelResponse(int serverPort, UUID accessTokenId, UUID labelId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_GET_LABEL, "labelId", labelId));
    }

    public static Response getEditLabelResponse(int serverPort, UUID accessTokenId, UUID labelId, String label) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(label))
            .post(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_EDIT_LABEL, "labelId", labelId));
    }

    public static List<LabelResponse> editLabel(int serverPort, UUID accessTokenId, UUID labelId, String label){
        Response response = getEditLabelResponse(serverPort, accessTokenId, labelId, label);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(LabelResponse[].class));
    }

    public static List<LabelResponse> deleteLabel(int serverPort, UUID accessTokenId, UUID labelId) {
        Response response = getDeleteLabelResponse(serverPort, accessTokenId, labelId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(LabelResponse[].class));
    }

    public static Response getDeleteLabelResponse(int serverPort, UUID accessTokenId, UUID labelId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_DELETE_LABEL, "labelId", labelId));
    }
}
