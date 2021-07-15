package com.github.saphyra.apphub.service.skyxplore.game.service.solar_system;

import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.PlanetLocationResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class PlanetToLocationResponseConverter {
    List<PlanetLocationResponse> mapPlanets(Collection<Planet> planets, Game game) {
        return planets.stream()
            .map(planet -> mapPlanet(planet, game))
            .collect(Collectors.toList());
    }

    private PlanetLocationResponse mapPlanet(Planet planet, Game game) {
        return PlanetLocationResponse.builder()
            .planetId(planet.getPlanetId())
            .planetName(planet.getDefaultName())
            .coordinate(planet.getCoordinate().getCoordinate())
            .owner(planet.getOwner())
            .ownerName(Optional.ofNullable(planet.getOwner()).map(owner -> game.getPlayers().get(owner).getUsername()).orElse(null))
            .build();
    }
}
