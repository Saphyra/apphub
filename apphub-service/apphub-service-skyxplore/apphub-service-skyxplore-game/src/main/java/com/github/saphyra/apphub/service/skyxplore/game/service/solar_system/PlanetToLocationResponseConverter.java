package com.github.saphyra.apphub.service.skyxplore.game.service.solar_system;

import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.PlanetLocationResponse;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.visibility.VisibilityFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class PlanetToLocationResponseConverter {
    private final VisibilityFacade visibilityFacade;

    List<PlanetLocationResponse> mapPlanets(Game game, UUID solarSystemId, UUID userId) {
        return game.getData()
            .getPlanets()
            .getBySolarSystemId(solarSystemId)
            .stream()
            .filter(planet -> visibilityFacade.isVisible(userId, planet))
            .map(planet -> mapPlanet(game, planet, userId))
            .collect(Collectors.toList());
    }

    private PlanetLocationResponse mapPlanet(Game game, Planet planet, UUID userId) {
        Coordinate coordinate = game.getData()
            .getCoordinates()
            .findByReferenceId(planet.getPlanetId());

        return PlanetLocationResponse.builder()
            .planetId(planet.getPlanetId())
            .planetName(planet.getCustomNames().getOptional(userId).orElse(planet.getDefaultName()))
            .coordinate(coordinate)
            .owner(planet.getOwner())
            .ownerName(Optional.ofNullable(planet.getOwner()).map(owner -> game.getPlayers().get(owner).getPlayerName()).orElse(null))
            .build();
    }
}
