package com.github.saphyra.apphub.integration.action.backend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.saphyra.apphub.integration.core.TestBase;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
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
    public static void changeLanguage(Language language, UUID accessTokenId, String locale) {
        Response response = getChangeLanguageResponse(language, accessTokenId, locale);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getChangeLanguageResponse(Language locale, UUID accessTokenId, String newLocale) {
        return RequestFactory.createAuthorizedRequest(locale, accessTokenId)
            .body(new OneParamRequest<>(newLocale))
            .post(UrlFactory.create(Endpoints.ACCOUNT_CHANGE_LANGUAGE));
    }

    public static List<LanguageResponse> getLanguages(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.ACCOUNT_GET_LANGUAGES));

        TypeReference<List<LanguageResponse>> ref = new TypeReference<>() {
        };
        return TestBase.OBJECT_MAPPER_WRAPPER.readValue(response.getBody().asString(), ref);
    }

    public static Response getChangeEmailResponse(Language language, UUID accessTokenId, ChangeEmailRequest request) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(request)
            .post(UrlFactory.create(Endpoints.ACCOUNT_CHANGE_EMAIL));
    }

    public static Response getChangeUsernameResponse(Language locale, UUID accessTokenId, ChangeUsernameRequest request) {
        return RequestFactory.createAuthorizedRequest(locale, accessTokenId)
            .body(request)
            .post(UrlFactory.create(Endpoints.ACCOUNT_CHANGE_USERNAME));
    }

    public static Response getChangePasswordResponse(Language locale, UUID accessTokenId, ChangePasswordRequest request) {
        return RequestFactory.createAuthorizedRequest(locale, accessTokenId)
            .body(request)
            .post(UrlFactory.create(Endpoints.ACCOUNT_CHANGE_PASSWORD));
    }

    public static void deleteAccount(Language language, UUID accessTokenId, String password) {
        Response response = getDeleteAccountResponse(language, accessTokenId, new OneParamRequest<>(password));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteAccountResponse(Language locale, UUID accessTokenId, OneParamRequest<String> request) {
        return RequestFactory.createAuthorizedRequest(locale, accessTokenId)
            .body(request)
            .delete(UrlFactory.create(Endpoints.ACCOUNT_DELETE_ACCOUNT));
    }
}
