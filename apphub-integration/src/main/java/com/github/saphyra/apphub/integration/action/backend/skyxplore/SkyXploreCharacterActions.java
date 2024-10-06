package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreDataEndpoints;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreCharacterActions {
    public static void createOrUpdateCharacter(int serverPort, UUID accessTokenId, SkyXploreCharacterModel model) {
        assertThat(getCreateCharacterResponse(serverPort, accessTokenId, model).getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateCharacterResponse(int serverPort, UUID accessTokenId, SkyXploreCharacterModel model) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(model)
            .post(UrlFactory.create(serverPort, SkyXploreDataEndpoints.SKYXPLORE_CREATE_OR_UPDATE_CHARACTER));
    }

    public static String getCharacterName(String email) {
        return DatabaseUtil.findSkyXploreCharacterByEmail(email)
            .orElseThrow(() -> new RuntimeException("SkyXploreCharacter not found for email " + email));
    }

    public static Response getCharacterNameResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, SkyXploreDataEndpoints.SKYXPLORE_GET_CHARACTER_NAME));
    }

    public static Response getExistsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, SkyXploreDataEndpoints.SKYXPLORE_CHARACTER_EXISTS));
    }
}
