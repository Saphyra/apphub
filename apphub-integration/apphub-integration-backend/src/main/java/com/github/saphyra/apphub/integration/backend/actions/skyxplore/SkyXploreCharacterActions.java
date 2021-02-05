package com.github.saphyra.apphub.integration.backend.actions.skyxplore;

import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
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
