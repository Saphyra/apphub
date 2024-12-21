package com.github.saphyra.apphub.integration.action.backend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreGameEndpoints;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.MapResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.MapSolarSystemResponse;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreMapActions {
    public static MapResponse getMap(int serverPort, UUID accessTokenId) {
        Response response = getMapResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(MapResponse.class);
    }

    public static Response getMapResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_GAME_MAP));
    }

    public static MapSolarSystemResponse getSolarSystem(int serverPort, UUID accessTokenId) {
        return getMap(serverPort, accessTokenId)
            .getSolarSystems()
            .stream()
            .findAny()
            .orElseThrow(() -> new RuntimeException("No visible SolarSystem"));
    }

    public static MapSolarSystemResponse getSolarSystem(int serverPort, UUID accessTokenId, UUID solarSystemId) {
        return getMap(serverPort, accessTokenId)
            .getSolarSystems()
            .stream()
            .filter(response -> response.getSolarSystemId().equals(solarSystemId))
            .findAny()
            .orElseThrow(() -> new RuntimeException("No SolarSystem found with id " + solarSystemId));
    }
}
