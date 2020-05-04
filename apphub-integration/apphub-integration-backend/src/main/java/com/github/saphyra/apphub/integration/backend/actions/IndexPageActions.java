package com.github.saphyra.apphub.integration.backend.actions;

import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.Endpoint;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.model.LoginRequest;
import com.github.saphyra.apphub.integration.common.model.LoginResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationRequest;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexPageActions {
    public static void registerUser(RegistrationRequest registrationRequest) {
        Response response = getRegistrationResponse(registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getRegistrationResponse(RegistrationRequest registrationRequest) {
        return RequestFactory.createRequest()
            .body(registrationRequest)
            .post(UrlFactory.create(TestBase.SERVER_PORT, Endpoint.REGISTER));
    }

    public static UUID login(LoginRequest loginRequest) {
        Response response = getLoginResponse(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody()
            .as(LoginResponse.class)
            .getAccessTokenId();
    }

    public static LoginResponse getSuccessfulLoginResponse(LoginRequest loginRequest) {
        Response response = getLoginResponse(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody()
            .as(LoginResponse.class);
    }

    public static Response getLoginResponse(LoginRequest loginRequest) {
        return RequestFactory.createRequest()
            .body(loginRequest)
            .post(UrlFactory.create(TestBase.SERVER_PORT, Endpoint.LOGIN));
    }
}