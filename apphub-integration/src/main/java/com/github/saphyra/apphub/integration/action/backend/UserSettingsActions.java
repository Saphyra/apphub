package com.github.saphyra.apphub.integration.action.backend;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.StringStringMap;
import com.github.saphyra.apphub.integration.structure.api.user.SetUserSettingsRequest;
import io.restassured.response.Response;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UserSettingsActions {
    public static Map<String, String> getUserSettings(int serverPort, UUID accessTokenId, String category) {
        Response response = getQueryUserSettingsResponse(serverPort, accessTokenId, category);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(StringStringMap.class);
    }

    public static Response getQueryUserSettingsResponse(int serverPort, UUID accessTokenId, String category) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.GET_USER_SETTINGS, "category", category));
    }

    public static Map<String, String> setUserSetting(int serverPort, UUID accessTokenId, SetUserSettingsRequest request) {
        Response response = getUpdateUserSettingsResponse(serverPort, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(StringStringMap.class);
    }

    public static Response getUpdateUserSettingsResponse(int serverPort, UUID accessTokenId, SetUserSettingsRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.SET_USER_SETTINGS));
    }
}
