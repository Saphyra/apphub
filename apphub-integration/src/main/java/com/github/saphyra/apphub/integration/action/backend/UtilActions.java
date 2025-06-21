package com.github.saphyra.apphub.integration.action.backend;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.UserEndpoints;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilActions {
    public static boolean isUserAdmin(int serverPort, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, UserEndpoints.IS_ADMIN));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().jsonPath().getBoolean("value");
    }
}
