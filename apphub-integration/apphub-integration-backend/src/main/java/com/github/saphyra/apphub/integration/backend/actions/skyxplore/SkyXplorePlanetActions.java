package com.github.saphyra.apphub.integration.backend.actions.skyxplore;

import com.github.saphyra.apphub.integration.backend.model.skyxplore.MapSolarSystemResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.PlanetLocationResponse;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;

import java.util.UUID;

import static java.util.Objects.isNull;

public class SkyXplorePlanetActions {
    public static PlanetLocationResponse getPopulatedPlanet(Language language, UUID accessTokenId) {

        return SkyXploreMapActions.getMap(language, accessTokenId)
            .getSolarSystems()
            .stream()
            .map(MapSolarSystemResponse::getSolarSystemId)
            .flatMap(solarSystemId -> SkyXploreSolarSystemActions.getSolarSystem(language, accessTokenId, solarSystemId).getPlanets().stream())
            .filter(planetLocationResponse -> !isNull(planetLocationResponse.getOwner()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No populated planet found."));
    }
}
