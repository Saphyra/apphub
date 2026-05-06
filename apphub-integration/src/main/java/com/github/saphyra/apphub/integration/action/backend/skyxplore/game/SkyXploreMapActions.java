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
    public static MapResponse getMap(int serverPort, String accessToken) {
        Response response = getMapResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(MapResponse.class);
    }

    public static Response getMapResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_GAME_MAP));
    }

    public static MapSolarSystemResponse getSolarSystem(int serverPort, String accessToken) {
        return getMap(serverPort, accessToken)
            .getSolarSystems()
            .stream()
            .findAny()
            .orElseThrow(() -> new RuntimeException("No visible SolarSystem"));
    }

    public static MapSolarSystemResponse getSolarSystem(int serverPort, String accessToken, UUID solarSystemId) {
        return getMap(serverPort, accessToken)
            .getSolarSystems()
            .stream()
            .filter(response -> response.getSolarSystemId().equals(solarSystemId))
            .findAny()
            .orElseThrow(() -> new RuntimeException("No SolarSystem found with id " + solarSystemId));
    }
}
