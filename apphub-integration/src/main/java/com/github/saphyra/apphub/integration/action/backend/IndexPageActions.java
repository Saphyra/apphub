package com.github.saphyra.apphub.integration.action.backend;

import com.github.saphyra.apphub.integration.core.TestBase;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.user.LoginRequest;
import com.github.saphyra.apphub.integration.structure.user.LoginResponse;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.user.RegistrationRequest;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexPageActions {
    public static UUID registerAndLogin(Language locale, RegistrationParameters userData) {
        registerUser(locale, userData.toRegistrationRequest());
        return login(locale, userData.toLoginRequest());
    }

    public static void registerUser(Language locale, RegistrationRequest registrationRequest) {
        Response response = getRegistrationResponse(locale, registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getRegistrationResponse(Language locale, RegistrationRequest registrationRequest) {
        return RequestFactory.createRequest(locale)
            .body(registrationRequest)
            .post(UrlFactory.create(TestBase.SERVER_PORT, Endpoints.ACCOUNT_REGISTER));
    }

    public static UUID login(Language locale, String email, String password) {
        return login(locale, LoginRequest.builder().email(email).password(password).build());
    }

    public static UUID login(Language locale, LoginRequest loginRequest) {
        Response response = getLoginResponse(locale, loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody()
            .as(LoginResponse.class)
            .getAccessTokenId();
    }

    public static LoginResponse getSuccessfulLoginResponse(Language locale, LoginRequest loginRequest) {
        Response response = getLoginResponse(locale, loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody()
            .as(LoginResponse.class);
    }

    public static Response getLoginResponse(Language locale, LoginRequest loginRequest) {
        return RequestFactory.createRequest(locale)
            .body(loginRequest)
            .post(UrlFactory.create(TestBase.SERVER_PORT, Endpoints.LOGIN));
    }
}