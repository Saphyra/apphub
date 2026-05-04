package com.github.saphyra.apphub.integration.action.backend;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.UserEndpoints;
import com.github.saphyra.apphub.integration.structure.api.StringStringMap;
import com.github.saphyra.apphub.integration.structure.api.user.SetUserSettingsRequest;
import io.restassured.response.Response;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UserSettingsActions {
    public static Map<String, String> getUserSettings(int serverPort, String accessToken, String category) {
        Response response = getQueryUserSettingsResponse(serverPort, accessToken, category);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(StringStringMap.class);
    }

    public static Response getQueryUserSettingsResponse(int serverPort, String accessToken, String category) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, UserEndpoints.GET_USER_SETTINGS, "category", category));
    }

    public static Map<String, String> setUserSetting(int serverPort, String accessToken, SetUserSettingsRequest request) {
        Response response = getUpdateUserSettingsResponse(serverPort, accessToken, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(StringStringMap.class);
    }

    public static Response getUpdateUserSettingsResponse(int serverPort, String accessToken, SetUserSettingsRequest request) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(request)
            .post(UrlFactory.create(serverPort, UserEndpoints.SET_USER_SETTINGS));
    }
}
