package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.MapSolarSystemResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetLocationResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SolarSystemResponse;
import io.restassured.response.Response;

import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreSolarSystemActions {
    public static PlanetLocationResponse getPopulatedPlanet(Language language, UUID accessTokenId) {
        return SkyXploreMapActions.getMap(language, accessTokenId)
            .getSolarSystems()
            .stream()
            .map(MapSolarSystemResponse::getSolarSystemId)
            .flatMap(solarSystemId -> getSolarSystem(language, accessTokenId, solarSystemId).getPlanets().stream())
            .filter(planetLocationResponse -> !isNull(planetLocationResponse.getOwner()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No populated planet found."));
    }

    public static SolarSystemResponse getSolarSystem(Language language, UUID accessTokenId, UUID solarSystemId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_GET_SOLAR_SYSTEM, "solarSystemId", solarSystemId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(SolarSystemResponse.class);
    }

    public static Response getRenameSolarSystemResponse(Language language, UUID accessTokenId, UUID solarSystemId, String solarSystemName) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(solarSystemName))
            .post(UrlFactory.create(Endpoints.SKYXPLORE_SOLAR_SYSTEM_RENAME, "solarSystemId", solarSystemId));
    }

    public static void renameSolarSystem(Language language, UUID accessTokenId, UUID solarSystemId, String solarSystemName) {
        Response response = getRenameSolarSystemResponse(language, accessTokenId, solarSystemId, solarSystemName);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static PlanetLocationResponse findPlanet(Language language, UUID accessTokenId, UUID solarSystemId, UUID planetId) {
        return getSolarSystem(language, accessTokenId, solarSystemId)
            .getPlanets()
            .stream()
            .filter(planetLocationResponse -> planetLocationResponse.getPlanetId().equals(planetId))
            .findAny()
            .orElseThrow(() -> new RuntimeException("Planet " + planetId + " not found in SolarSystem " + solarSystemId));
    }
}
