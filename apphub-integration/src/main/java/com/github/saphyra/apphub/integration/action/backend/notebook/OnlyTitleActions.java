package com.github.saphyra.apphub.integration.action.backend.notebook;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateOnlyTitleRequest;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OnlyTitleActions {
    public static UUID createOnlyTitle(UUID accessTokenId, CreateOnlyTitleRequest request) {
        Response response = getCreateOnlyTitleResponse(accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().jsonPath().getUUID("value");
    }

    public static Response getCreateOnlyTitleResponse(UUID accessTokenId, CreateOnlyTitleRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.NOTEBOOK_CREATE_ONLY_TITLE));
    }
}
