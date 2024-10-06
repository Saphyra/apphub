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
    public static Response getChangeEmailResponse(int serverPort, UUID accessTokenId, ChangeEmailRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(serverPort, UserEndpoints.ACCOUNT_CHANGE_EMAIL));
    }

    public static Response getChangeUsernameResponse(int serverPort, UUID accessTokenId, ChangeUsernameRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(serverPort, UserEndpoints.ACCOUNT_CHANGE_USERNAME));
    }

    public static Response getChangePasswordResponse(int serverPort, UUID accessTokenId, ChangePasswordRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(serverPort, UserEndpoints.ACCOUNT_CHANGE_PASSWORD));
    }

    public static void deleteAccount(int serverPort, UUID accessTokenId, String password) {
        Response response = getDeleteAccountResponse(serverPort, accessTokenId, new OneParamRequest<>(password));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteAccountResponse(int serverPort, UUID accessTokenId, OneParamRequest<String> request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .delete(UrlFactory.create(serverPort, UserEndpoints.ACCOUNT_DELETE_ACCOUNT));
    }

    public static void changePassword(int serverPort, UUID accessTokenId, ChangePasswordRequest request) {
        Response response = getChangePasswordResponse(serverPort, accessTokenId, request);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
