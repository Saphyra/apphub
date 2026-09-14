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
    public static Response getCreateLabelResponse(int serverPort, String accessToken, String label) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(label))
            .put(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_CREATE_LABEL));
    }

    public static LabelResponse createLabel(int serverPort, String accessToken, String label) {
        Response response = getCreateLabelResponse(serverPort, accessToken, label);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(LabelResponse.class);
    }

    public static List<LabelResponse> getLabels(int serverPort, String accessToken) {
        Response response = getGetLabelsResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(LabelResponse[].class));
    }

    public static Response getGetLabelsResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_GET_LABELS));
    }

    public static LabelResponse getLabel(int serverPort, String accessToken, UUID labelId) {
        Response response = getGetLabelResponse(serverPort, accessToken, labelId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(LabelResponse.class);
    }

    public static Response getGetLabelResponse(int serverPort, String accessToken, UUID labelId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_GET_LABEL, "labelId", labelId));
    }

    public static Response getEditLabelResponse(int serverPort, String accessToken, UUID labelId, String label) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(label))
            .post(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_EDIT_LABEL, "labelId", labelId));
    }

    public static List<LabelResponse> editLabel(int serverPort, String accessToken, UUID labelId, String label){
        Response response = getEditLabelResponse(serverPort, accessToken, labelId, label);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(LabelResponse[].class));
    }

    public static List<LabelResponse> deleteLabel(int serverPort, String accessToken, UUID labelId) {
        Response response = getDeleteLabelResponse(serverPort, accessToken, labelId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(LabelResponse[].class));
    }

    public static Response getDeleteLabelResponse(int serverPort, String accessToken, UUID labelId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_DELETE_LABEL, "labelId", labelId));
    }

    public static Response getLabelsOfEventResponse(int serverPort, String accessToken, UUID eventId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_GET_LABELS_OF_EVENT, "eventId", eventId));
    }

    public static List<LabelResponse> getLabelsOfEvent(int serverPort, String accessToken, UUID labelId) {
        Response response = getLabelsOfEventResponse(serverPort, accessToken, labelId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(LabelResponse[].class));
    }
}
