package com.github.saphyra.apphub.integration.action.backend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.saphyra.apphub.integration.core.TestBase;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.LanguageResponse;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.user.ChangeEmailRequest;
import com.github.saphyra.apphub.integration.structure.api.user.ChangePasswordRequest;
import com.github.saphyra.apphub.integration.structure.api.user.ChangeUsernameRequest;
import io.restassured.response.Response;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountActions {
    public static void changeLanguage(UUID accessTokenId, String locale) {
        Response response = getChangeLanguageResponse(accessTokenId, locale);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getChangeLanguageResponse(UUID accessTokenId, String newLocale) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(newLocale))
            .post(UrlFactory.create(Endpoints.ACCOUNT_CHANGE_LANGUAGE));
    }

    public static List<LanguageResponse> getLanguages(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.ACCOUNT_GET_LANGUAGES));

        TypeReference<List<LanguageResponse>> ref = new TypeReference<>() {
        };
        return TestBase.OBJECT_MAPPER_WRAPPER.readValue(response.getBody().asString(), ref);
    }

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
}
