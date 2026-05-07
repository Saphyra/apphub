package com.github.saphyra.apphub.integration.action.backend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreGameEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.MapSolarSystemResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetLocationResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SolarSystemResponse;
import io.restassured.response.Response;

import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreSolarSystemActions {
    public static PlanetLocationResponse getPopulatedPlanet(int serverPort, String accessToken) {
        return SkyXploreMapActions.getMap(serverPort, accessToken)
            .getSolarSystems()
            .stream()
            .map(MapSolarSystemResponse::getSolarSystemId)
            .flatMap(solarSystemId -> getSolarSystem(serverPort, accessToken, solarSystemId).getPlanets().stream())
            .filter(planetLocationResponse -> !isNull(planetLocationResponse.getOwner()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No populated planet found."));
    }

    public static SolarSystemResponse getSolarSystem(int serverPort, String accessToken, UUID solarSystemId) {
        Response response = getSolarSystemResponse(serverPort, accessToken, solarSystemId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(SolarSystemResponse.class);
    }

    public static Response getSolarSystemResponse(int serverPort, String accessToken, UUID solarSystemId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_GET_SOLAR_SYSTEM, "solarSystemId", solarSystemId));
    }

    public static Response getRenameSolarSystemResponse(int serverPort, String accessToken, UUID solarSystemId, String solarSystemName) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(solarSystemName))
            .post(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_SOLAR_SYSTEM_RENAME, "solarSystemId", solarSystemId));
    }

    public static void renameSolarSystem(int serverPort, String accessToken, UUID solarSystemId, String solarSystemName) {
        Response response = getRenameSolarSystemResponse(serverPort, accessToken, solarSystemId, solarSystemName);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static PlanetLocationResponse findPlanet(int serverPort, String accessToken, UUID solarSystemId, UUID planetId) {
        return getSolarSystem(serverPort, accessToken, solarSystemId)
            .getPlanets()
            .stream()
            .filter(planetLocationResponse -> planetLocationResponse.getPlanetId().equals(planetId))
            .findAny()
            .orElseThrow(() -> new RuntimeException("Planet " + planetId + " not found in SolarSystem " + solarSystemId));
    }
}
