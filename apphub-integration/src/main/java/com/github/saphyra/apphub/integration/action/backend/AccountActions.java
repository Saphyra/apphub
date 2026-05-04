package com.github.saphyra.apphub.integration.action.backend;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.UserEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.user.ChangeEmailRequest;
import com.github.saphyra.apphub.integration.structure.api.user.ChangePasswordRequest;
import com.github.saphyra.apphub.integration.structure.api.user.ChangeUsernameRequest;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountActions {
    public static Response getChangeEmailResponse(int serverPort, String accessToken, ChangeEmailRequest request) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(request)
            .post(UrlFactory.create(serverPort, UserEndpoints.ACCOUNT_CHANGE_EMAIL));
    }

    public static Response getChangeUsernameResponse(int serverPort, String accessToken, ChangeUsernameRequest request) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(request)
            .post(UrlFactory.create(serverPort, UserEndpoints.ACCOUNT_CHANGE_USERNAME));
    }

    public static Response getChangePasswordResponse(int serverPort, String accessToken, ChangePasswordRequest request) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(request)
            .post(UrlFactory.create(serverPort, UserEndpoints.ACCOUNT_CHANGE_PASSWORD));
    }

    public static void deleteAccount(int serverPort, String accessToken, String password) {
        Response response = getDeleteAccountResponse(serverPort, accessToken, new OneParamRequest<>(password));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteAccountResponse(int serverPort, String accessToken, OneParamRequest<String> request) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(request)
            .delete(UrlFactory.create(serverPort, UserEndpoints.ACCOUNT_DELETE_ACCOUNT));
    }

    public static void changePassword(int serverPort, String accessToken, ChangePasswordRequest request) {
        Response response = getChangePasswordResponse(serverPort, accessToken, request);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
