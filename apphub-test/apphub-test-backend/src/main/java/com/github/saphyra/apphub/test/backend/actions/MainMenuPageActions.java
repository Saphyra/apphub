package com.github.saphyra.apphub.test.backend.actions;

import com.github.saphyra.apphub.lib.endpoint.Endpoint;
import com.github.saphyra.apphub.test.common.integration.TestBase;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class MainMenuPageActions {
    public static void logout(UUID accessTokenId) {
        Response response = getLogoutResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static Response getLogoutResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(TestBase.SERVER_PORT, Endpoint.LOGOUT));
    }
}
