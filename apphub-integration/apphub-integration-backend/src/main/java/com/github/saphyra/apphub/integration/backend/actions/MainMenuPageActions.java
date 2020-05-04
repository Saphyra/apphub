package com.github.saphyra.apphub.integration.backend.actions;

import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.Endpoint;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class MainMenuPageActions {
    public static void logout(UUID accessTokenId) {
        Response response = getLogoutResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getLogoutResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(TestBase.SERVER_PORT, Endpoint.LOGOUT));
    }
}
