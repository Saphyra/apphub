package com.github.saphyra.apphub.integration.backend.actions.skyxplore;

import com.github.saphyra.apphub.integration.backend.model.skyxplore.MapResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.MapSolarSystemResponse;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreMapActions {
    public static MapResponse getMap(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_GAME_MAP));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(MapResponse.class);
    }

    public static MapSolarSystemResponse getSolarSystem(Language language, UUID accessTokenId) {
        return getMap(language, accessTokenId)
            .getSolarSystems()
            .stream()
            .findAny()
            .orElseThrow(() -> new RuntimeException("No visible SolarSystem"));
    }

    public static MapSolarSystemResponse getSolarSystem(Language language, UUID accessTokenId, UUID solarSystemId) {
        return getMap(language, accessTokenId)
            .getSolarSystems()
            .stream()
            .filter(response -> response.getSolarSystemId().equals(solarSystemId))
            .findAny()
            .orElseThrow(() -> new RuntimeException("No SolarSystem found with id " + solarSystemId));
    }
}
