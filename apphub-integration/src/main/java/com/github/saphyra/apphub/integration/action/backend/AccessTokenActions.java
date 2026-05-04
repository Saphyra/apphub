package com.github.saphyra.apphub.integration.action.backend;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.AuthorizationEndpoints;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class AccessTokenActions {
    public static TokenResponse refresh(int serverPort, String refreshToken) {
        Response response = RequestFactory.createRefreshRequest(refreshToken)
            .post(UrlFactory.create(serverPort, AuthorizationEndpoints.REFRESH));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody()
            .as(TokenResponse.class);
    }

    public void invalidateAccessToken(int serverPort, String accessToken) {
        Response response = RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, "/invalidate-access-token/rest"));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
