package com.github.saphyra.apphub.integration.backend.actions.skyxplore;

import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.OneParamRequest;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXplorePlanetActions {
    public static Response getRenamePlanetResponse(Language language, UUID accessTokenId, UUID planetId, String planetName) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(planetName))
            .post(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_RENAME, "planetId", planetId));
    }

    public static void renamePlanet(Language language, UUID accessTokenId, UUID planetId, String planetName) {
        Response response = getRenamePlanetResponse(language, accessTokenId, planetId, planetName);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
