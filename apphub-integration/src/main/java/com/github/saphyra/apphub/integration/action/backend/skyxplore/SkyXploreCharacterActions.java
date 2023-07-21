package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreCharacterActions {
    public static void createOrUpdateCharacter(Language language, UUID accessTokenId, SkyXploreCharacterModel model) {
        assertThat(getCreateCharacterResponse(language, accessTokenId, model).getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateCharacterResponse(Language language, UUID accessTokenId, SkyXploreCharacterModel model) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(model)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_CREATE_OR_UPDATE_CHARACTER));
    }

    public static String getCharacterName(String email) {
        return DatabaseUtil.findSkyXploreCharacterByEmail(email)
            .orElseThrow(() -> new RuntimeException("SkyXploreCharacter not found for email " + email));
    }
}
