package com.github.saphyra.apphub.integration.action.backend;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.user.ChangeEmailRequest;
import com.github.saphyra.apphub.integration.structure.api.user.ChangePasswordRequest;
import com.github.saphyra.apphub.integration.structure.api.user.ChangeUsernameRequest;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountActions {
    public static Response getChangeEmailResponse(UUID accessTokenId, ChangeEmailRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(Endpoints.ACCOUNT_CHANGE_EMAIL));
    }

    public static Response getChangeUsernameResponse(UUID accessTokenId, ChangeUsernameRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(Endpoints.ACCOUNT_CHANGE_USERNAME));
    }

    public static Response getChangePasswordResponse(UUID accessTokenId, ChangePasswordRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(Endpoints.ACCOUNT_CHANGE_PASSWORD));
    }

    public static void deleteAccount(UUID accessTokenId, String password) {
        Response response = getDeleteAccountResponse(accessTokenId, new OneParamRequest<>(password));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteAccountResponse(UUID accessTokenId, OneParamRequest<String> request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .delete(UrlFactory.create(Endpoints.ACCOUNT_DELETE_ACCOUNT));
    }

    public static void changePassword(UUID accessTokenId, ChangePasswordRequest request) {
        Response response = getChangePasswordResponse(accessTokenId, request);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
