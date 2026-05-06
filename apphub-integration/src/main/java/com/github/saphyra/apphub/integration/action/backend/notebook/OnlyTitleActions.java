package com.github.saphyra.apphub.integration.action.backend.notebook;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.NotebookEndpoints;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateOnlyTitleRequest;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OnlyTitleActions {
    public static UUID createOnlyTitle(int serverPort, String accessToken, CreateOnlyTitleRequest request) {
        Response response = getCreateOnlyTitleResponse(serverPort, accessToken, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().jsonPath().getUUID("value");
    }

    public static Response getCreateOnlyTitleResponse(int serverPort, String accessToken, CreateOnlyTitleRequest request) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(request)
            .put(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_CREATE_ONLY_TITLE));
    }
}
