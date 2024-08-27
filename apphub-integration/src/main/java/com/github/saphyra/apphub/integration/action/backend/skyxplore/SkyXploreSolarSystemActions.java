package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.MapSolarSystemResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetLocationResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SolarSystemResponse;
import io.restassured.response.Response;

import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreSolarSystemActions {
    public static PlanetLocationResponse getPopulatedPlanet(int serverPort, UUID accessTokenId) {
        return SkyXploreMapActions.getMap(serverPort, accessTokenId)
            .getSolarSystems()
            .stream()
            .map(MapSolarSystemResponse::getSolarSystemId)
            .flatMap(solarSystemId -> getSolarSystem(serverPort, accessTokenId, solarSystemId).getPlanets().stream())
            .filter(planetLocationResponse -> !isNull(planetLocationResponse.getOwner()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No populated planet found."));
    }

    public static SolarSystemResponse getSolarSystem(int serverPort, UUID accessTokenId, UUID solarSystemId) {
        Response response = getSolarSystemResponse(serverPort, accessTokenId, solarSystemId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(SolarSystemResponse.class);
    }

    public static Response getSolarSystemResponse(int serverPort, UUID accessTokenId, UUID solarSystemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GET_SOLAR_SYSTEM, "solarSystemId", solarSystemId));
    }

    public static Response getRenameSolarSystemResponse(int serverPort, UUID accessTokenId, UUID solarSystemId, String solarSystemName) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(solarSystemName))
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_SOLAR_SYSTEM_RENAME, "solarSystemId", solarSystemId));
    }

    public static void renameSolarSystem(int serverPort, UUID accessTokenId, UUID solarSystemId, String solarSystemName) {
        Response response = getRenameSolarSystemResponse(serverPort, accessTokenId, solarSystemId, solarSystemName);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static PlanetLocationResponse findPlanet(int serverPort, UUID accessTokenId, UUID solarSystemId, UUID planetId) {
        return getSolarSystem(serverPort, accessTokenId, solarSystemId)
            .getPlanets()
            .stream()
            .filter(planetLocationResponse -> planetLocationResponse.getPlanetId().equals(planetId))
            .findAny()
            .orElseThrow(() -> new RuntimeException("Planet " + planetId + " not found in SolarSystem " + solarSystemId));
    }
}
