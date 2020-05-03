package com.github.saphyra.apphub.test.backend.actions;

import com.github.saphyra.apphub.api.user.authentication.model.request.LoginRequest;
import com.github.saphyra.apphub.api.user.authentication.model.response.LoginResponse;
import com.github.saphyra.apphub.api.user.data.model.request.RegistrationRequest;
import com.github.saphyra.apphub.lib.endpoint.Endpoint;
import com.github.saphyra.apphub.test.common.integration.TestBase;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


public class IndexPageActions {
    public static void registerUser(RegistrationRequest registrationRequest) {
        Response response = getRegistrationResponse(registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static Response getRegistrationResponse(RegistrationRequest registrationRequest) {
        return RequestFactory.createRequest()
            .body(registrationRequest)
            .post(UrlFactory.create(TestBase.SERVER_PORT, Endpoint.REGISTER));
    }

    public static UUID login(LoginRequest loginRequest) {
        Response response = getLoginResponse(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        return response.getBody()
            .as(LoginResponse.class)
            .getAccessTokenId();
    }

    public static LoginResponse getSuccessfulLoginResponse(LoginRequest loginRequest) {
        Response response = getLoginResponse(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        return response.getBody()
            .as(LoginResponse.class);
    }

    public static Response getLoginResponse(LoginRequest loginRequest) {
        return RequestFactory.createRequest()
            .body(loginRequest)
            .post(UrlFactory.create(TestBase.SERVER_PORT, Endpoint.LOGIN));
    }
}