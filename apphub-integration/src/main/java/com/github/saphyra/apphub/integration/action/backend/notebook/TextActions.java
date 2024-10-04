package com.github.saphyra.apphub.integration.action.backend.notebook;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.NotebookEndpoints;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.EditTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.TextResponse;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TextActions {
    public static UUID createText(int serverPort, UUID accessTokenId, CreateTextRequest request) {
        Response response = getCreateTextResponse(serverPort, accessTokenId, request);
        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().jsonPath().getUUID("value");
    }

    public static Response getCreateTextResponse(int serverPort, UUID accessTokenId, CreateTextRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_CREATE_TEXT));
    }

    public static TextResponse getText(int serverPort, UUID accessTokenId, UUID textId) {
        Response response = getTextResponse(serverPort, accessTokenId, textId);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(TextResponse.class);
    }

    public static Response getTextResponse(int serverPort, UUID accessTokenId, UUID textId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_GET_TEXT, "listItemId", textId));
    }

    public static void editText(int serverPort, UUID accessTokenId, UUID textId, EditTextRequest request) {
        Response response = getEditTextResponse(serverPort, accessTokenId, textId, request);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditTextResponse(int serverPort, UUID accessTokenId, UUID textId, EditTextRequest editTextRequest) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(editTextRequest)
            .post(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_EDIT_TEXT, "listItemId", textId));
    }
}
