package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreCharacterActions {
    public static void createOrUpdateCharacter(UUID accessTokenId, SkyXploreCharacterModel model) {
        assertThat(getCreateCharacterResponse(accessTokenId, model).getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateCharacterResponse(UUID accessTokenId, SkyXploreCharacterModel model) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(model)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_CREATE_OR_UPDATE_CHARACTER));
    }

    public static String getCharacterName(String email) {
        return DatabaseUtil.findSkyXploreCharacterByEmail(email)
            .orElseThrow(() -> new RuntimeException("SkyXploreCharacter not found for email " + email));
    }

    public static Response getCharacterNameResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_GET_CHARACTER_NAME));
    }

    public static Response getExistsResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_CHARACTER_EXISTS));
    }
}
