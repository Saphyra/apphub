package com.github.saphyra.apphub.integration.action.backend;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.UserEndpoints;
import com.github.saphyra.apphub.integration.structure.api.user.LoginRequest;
import com.github.saphyra.apphub.integration.structure.api.user.LoginResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationRequest;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexPageActions {
    public static UUID registerAndLogin(int serverPort, RegistrationParameters userData) {
        registerUser(serverPort, userData.toRegistrationRequest());
        return login(serverPort, userData.toLoginRequest());
    }

    public static void registerUser(int serverPort, RegistrationRequest registrationRequest) {
        Response response = getRegistrationResponse(serverPort, registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getRegistrationResponse(int serverPort, RegistrationRequest registrationRequest) {
        return RequestFactory.createRequest()
            .body(registrationRequest)
            .post(UrlFactory.create(serverPort, UserEndpoints.ACCOUNT_REGISTER));
    }

    public static UUID login(int serverPort, LoginRequest loginRequest) {
        Response response = getLoginResponse(serverPort, loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody()
            .as(LoginResponse.class)
            .getAccessTokenId();
    }

    public static LoginResponse getSuccessfulLoginResponse(int serverPort, LoginRequest loginRequest) {
        Response response = getLoginResponse(serverPort, loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody()
            .as(LoginResponse.class);
    }

    public static Response getLoginResponse(int serverPort, LoginRequest loginRequest) {
        return RequestFactory.createRequest()
            .body(loginRequest)
            .post(UrlFactory.create(serverPort, UserEndpoints.LOGIN));
    }
}