package com.github.saphyra.apphub.integration.backend.actions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.saphyra.apphub.integration.backend.model.account.ChangeEmailRequest;
import com.github.saphyra.apphub.integration.backend.model.account.ChangePasswordRequest;
import com.github.saphyra.apphub.integration.backend.model.account.ChangeUsernameRequest;
import com.github.saphyra.apphub.integration.backend.model.LanguageResponse;
import com.github.saphyra.apphub.integration.common.model.OneParamRequest;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import io.restassured.response.Response;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountPageActions {
    public static void changeLanguage(Language language, UUID accessTokenId, String locale) {
        Response response = getChangeLanguageResponse(language, accessTokenId, locale);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getChangeLanguageResponse(Language locale, UUID accessTokenId, String newLocale) {
        return RequestFactory.createAuthorizedRequest(locale, accessTokenId)
            .body(new OneParamRequest<>(newLocale))
            .post(UrlFactory.create(Endpoints.CHANGE_LANGUAGE));
    }

    public static List<LanguageResponse> getLanguages(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.GET_LANGUAGES));

        TypeReference<List<LanguageResponse>> ref = new TypeReference<List<LanguageResponse>>() {
        };
        return TestBase.OBJECT_MAPPER_WRAPPER.readValue(response.getBody().asString(), ref);
    }

    public static Response getChangeEmailResponse(Language language, UUID accessTokenId, ChangeEmailRequest request) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(request)
            .post(UrlFactory.create(Endpoints.CHANGE_EMAIL));
    }

    public static Response getChangeUsernameResponse(Language locale, UUID accessTokenId, ChangeUsernameRequest request) {
        return RequestFactory.createAuthorizedRequest(locale, accessTokenId)
            .body(request)
            .post(UrlFactory.create(Endpoints.CHANGE_USERNAME));
    }

    public static Response getChangePasswordResponse(Language locale, UUID accessTokenId, ChangePasswordRequest request) {
        return RequestFactory.createAuthorizedRequest(locale, accessTokenId)
            .body(request)
            .post(UrlFactory.create(Endpoints.CHANGE_PASSWORD));
    }

    public static Response getDeleteAccountResponse(Language locale, UUID accessTokenId, OneParamRequest<String> request) {
        return RequestFactory.createAuthorizedRequest(locale, accessTokenId)
            .body(request)
            .delete(UrlFactory.create(Endpoints.DELETE_ACCOUNT));
    }
}
