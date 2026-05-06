package com.github.saphyra.apphub.integration.action.backend;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.AuthorizationEndpoints;
import com.github.saphyra.apphub.integration.framework.endpoints.MainGatewayEndpoints;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class AccessTokenActions {
    public static TokenResponse refresh(int serverPort, String refreshToken) {
        Response response = getRefreshResponse(serverPort, refreshToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody()
            .as(TokenResponse.class);
    }

    public static Response getRefreshResponse(int serverPort, String refreshToken) {
        return RequestFactory.createRefreshRequest(refreshToken)
            .post(UrlFactory.create(serverPort, AuthorizationEndpoints.REFRESH));
    }

    public static Response getSessionCheckResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, MainGatewayEndpoints.MAIN_GATEWAY_CHECK_SESSION));
    }

    public void invalidateAccessToken(int serverPort, String accessToken) {
        Response response = RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, MainGatewayEndpoints.MAIN_GATEWAY_UTIL_INVALIDATE_ACCESS_TOKEN));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
