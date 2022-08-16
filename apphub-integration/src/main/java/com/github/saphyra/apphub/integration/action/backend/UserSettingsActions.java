package com.github.saphyra.apphub.integration.action.backend;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.user.SetUserSettingsRequest;
import com.github.saphyra.util.StringStringMap;
import io.restassured.response.Response;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UserSettingsActions {
    public static Map<String, String> getUserSettings(Language language, UUID accessTokenId, String category) {
        Response response = getQueryUserSettingsResponse(language, accessTokenId, category);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(StringStringMap.class);
    }

    public static Response getQueryUserSettingsResponse(Language language, UUID accessTokenId, String category) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.GET_USER_SETTINGS, "category", category));
    }

    public static void setUserSetting(Language language, UUID accessTokenId, SetUserSettingsRequest request) {
        Response response = getUpdateUserSettingsResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getUpdateUserSettingsResponse(Language language, UUID accessTokenId, SetUserSettingsRequest request) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(request)
            .post(UrlFactory.create(Endpoints.SET_USER_SETTINGS));
    }
}
