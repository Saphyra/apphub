package com.github.saphyra.apphub.integration.action.backend.notebook;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.framework.endpoints.NotebookEndpoints;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.EditTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.TextResponse;
import io.restassured.response.Response;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TextActions {
    public static UUID createText(int serverPort, String accessToken, CreateTextRequest request) {
        Response response = getCreateTextResponse(serverPort, accessToken, request);
        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().jsonPath().getUUID("value");
    }

    public static Response getCreateTextResponse(int serverPort, String accessToken, CreateTextRequest request) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(request)
            .put(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_CREATE_TEXT));
    }

    public static TextResponse getText(int serverPort, String accessToken, UUID textId) {
        Response response = getTextResponse(serverPort, accessToken, textId);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(TextResponse.class);
    }

    public static Response getTextResponse(int serverPort, String accessToken, UUID textId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_GET_TEXT, "listItemId", textId));
    }

    public static void editText(int serverPort, String accessToken, UUID textId, EditTextRequest request) {
        Response response = getEditTextResponse(serverPort, accessToken, textId, request);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditTextResponse(int serverPort, String accessToken, UUID textId, EditTextRequest editTextRequest) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(editTextRequest)
            .post(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_EDIT_TEXT, "listItemId", textId));
    }
}
